package com.android.albert.ng911;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.header.AcceptContactHeader;
import org.zoolu.sip.header.CSeqHeader;
import org.zoolu.sip.header.CallIdHeader;
import org.zoolu.sip.header.ContactHeader;
import org.zoolu.sip.header.DateHeader;
import org.zoolu.sip.header.ExpiresHeader;
import org.zoolu.sip.header.FromHeader;
import org.zoolu.sip.header.Header;
import org.zoolu.sip.header.MaxForwardsHeader;
import org.zoolu.sip.header.MultipleHeader;
import org.zoolu.sip.header.RequestLine;
import org.zoolu.sip.header.SipHeaders;
import org.zoolu.sip.header.UserAgentHeader;
import org.zoolu.sip.header.ViaHeader;
import org.zoolu.sip.message.BaseMessageFactory;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipMethods;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;
import org.zoolu.tools.Random;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alberto on 4/10/16.
 */
public class NG911MessageFactory extends BaseMessageFactory {

    /**
     *
     * NG9-1-1
     *
     * Creates a new INVITE request to start an emergency call
     *
     * Only used if user dials the emergency number (767)
     */
    public static Message createInviteNG911(String call_id, SipProvider sip_provider,
                                            SipURL request_uri, NameAddress to, NameAddress from,
                                            NameAddress contact, String body, String icsi, String location) {
        long cseq = SipProvider.pickInitialCSeq();
        String local_tag = SipProvider.pickTag();
        // String branch=SipStack.pickBranch();
        if (contact == null)
            contact = from;

        String method = SipMethods.INVITE;
        String remote_tag = null;
        String branch = null;

        String via_addr = sip_provider.getViaAddress();
        int host_port = sip_provider.getPort();
        boolean rport = sip_provider.isRportSet();
        String proto;
        if (request_uri.hasTransport())
            proto = request_uri.getTransport();
        else
            proto = sip_provider.getDefaultTransport();

        String qvalue = null;

        /**
         * We have 18 parameters:
         *      - SipProvider sip_provider (ARGUMENT)
         *      - String method (created)
         *      - SipURL request_uri (ARGUMENT)
         *      - NameAddress to (ARGUMENT)
         *      - NameAddress from (ARGUMENT)
         *      - NameAddress contact (ARGUMENT)
         *      - String proto (created)
         *      - String via_addr (created)
         *      - int host_port (created)
         *      - boolean rport (created)
         *      - String call_id (ARGUMENT)
         *      - long cseq (created)
         *      - String local_tag (created)
         *      - String remote_tag (created == null)
         *      - String branch (created == null)
         *      - String body (ARGUMENT)
         *      - String qvalue (created == null)
         *      - Sring icsi (ARGUMENT)
         *      - String location (ARGUMENT)
         *
         * Now using these parameters we'll create the message
         */

        //creation of new message
        Message req = new Message();
        // mandatory headers first (To, From, Via, Max-Forwards, Call-ID, CSeq):

        request_uri.setURL("urn:service:sos");  //NG911
        req.setRequestLine(new RequestLine(method, request_uri));

        ViaHeader via = new ViaHeader(proto, via_addr, host_port);
        if (rport)
            via.setRport();
        if (branch == null)
            branch = SipProvider.pickBranch();
        via.setBranch(branch);
        req.addViaHeader(via);
        req.setMaxForwardsHeader(new MaxForwardsHeader(70));

        //[NG911] Set 'To: ' header to urn:service:sos
        req.setHeader(new Header(SipHeaders.To, "urn:service:sos"));
        //NG911

        req.setFromHeader(new FromHeader(from, local_tag));
        req.setCallIdHeader(new CallIdHeader(call_id));
        req.setCSeqHeader(new CSeqHeader(cseq, method));
        // optional headers:
        // start modification by mandrajg
        if (contact != null) {
            if (((method == "REGISTER")||(method == "INVITE")) && (icsi != null) ){
                MultipleHeader contacts = new MultipleHeader(SipHeaders.Contact);
                contacts.addBottom(new ContactHeader(contact, qvalue, icsi));
                req.setContacts(contacts);
            }
            else{
                MultipleHeader contacts = new MultipleHeader(SipHeaders.Contact);
                contacts.addBottom(new ContactHeader(contact));
                req.setContacts(contacts);
            }
            // System.out.println("DEBUG: Contact: "+contact.toString());
        }
        if ((method == "INVITE") && (icsi != null) ){
            req.setAcceptContactHeader(new AcceptContactHeader(icsi));
        }
        // end modifications by mandrajg
        req.setExpiresHeader(new ExpiresHeader(String
                .valueOf(SipStack.default_expires)));
        // add User-Agent header field
        if (SipStack.ua_info != null)
            req.setUserAgentHeader(new UserAgentHeader(SipStack.ua_info));

        /**
         * Set custom headers for NG911
         */

        //Expires:
        if(!(req.hasHeader(SipHeaders.Expires))) req.setExpiresHeader(new ExpiresHeader(3600));
        //Accept-Language:
        Header acceptLanguage = new Header("Accept-Language", "en");
        req.setHeader(acceptLanguage);

        //Priority:
        Header priority = new Header("Priority", "emergency");
        req.setHeader(priority);

        //Geolocation:
        String from_uri = from.getAddress().toString().substring(4);    //remove 'sip:' part from the URI
        Header geolocation = new Header("Geolocation", "<cid:"+from_uri+">; inserted-by=\"" + from.getAddress().getHost() + "\"; used-for-routing");
        req.setHeader(geolocation);

        //Date:
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss ZZZZ");
        Date date = new Date();
        req.setDateHeader(new DateHeader(dateFormat.format(date)));

        /**
         * Add location to body
         *
         * Location will be added as part of a MIME message
         */

        String randomString = Random.nextString(50);
        String boundary = "NG911" + randomString;   //this is the boundary of the MIME body
        String mimeType = "multipart/mixed";

        //SDP part of the MIME body
        MIMEpart sdpMIME = new MIMEpart(call_id, "application/sdp", body);

        //Location part of the MIME body
            MIMEpart locationMIME = new MIMEpart(from_uri, "application/pidf+xml", location);

            //MIME body
            List<MIMEpart> content = new ArrayList<MIMEpart>();
            content.add(sdpMIME);
            content.add(locationMIME);
            MIMEmessage mimeBody = new MIMEmessage(mimeType, boundary, content);

            req.setBody(mimeType + "; boundary=" + boundary + "", mimeBody.toString());
        return req;
    }

}
