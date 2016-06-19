package com.android.albert.ng911;

public class MIMEpart {
    String version;
    String contentID;
    String contentType;
    String contentTransferEncoding;
    String content;

    /**
     * Constructor with all the fields
     *
     * @param version
     * @param contentID
     * @param contentType
     * @param contentTransferEncoding
     * @param content
     */
    public MIMEpart(String version, String contentID, String contentType, String contentTransferEncoding, String content) {
        this.version = version;
        this.contentID = contentID;
        this.contentType = contentType;
        this.contentTransferEncoding = contentTransferEncoding;
        this.content = content;
    }

    /**
     * Constructor without specifying MIME version
     *
     * @param contentID
     * @param contentType
     * @param contentTransferEncoding
     * @param content
     */
    public MIMEpart(String contentID, String contentType, String contentTransferEncoding, String content) {
        this.version = "1.0";
        this.contentID = contentID;
        this.contentType = contentType;
        this.contentTransferEncoding = contentTransferEncoding;
        this.content = content;
    }

    /**
     * Most basic constructor.
     *
     * @param contentID
     * @param contentType
     * @param content
     */
    public MIMEpart(String contentID, String contentType, String content) {
        this.version = "1.0";
        this.contentID = contentID;
        this.contentType = contentType;
        this.contentTransferEncoding = "8bit";
        this.content = content;
    }

    @Override
    public String toString() {
        String part = "";
        part += "\r\nMIME-Version: " + version;
        part += "\r\nContent-ID: <" + contentID + ">";
        part += "\r\nContent-Type: " + contentType;
        part += "\r\nContent-Transfer-Encoding: " + contentTransferEncoding + "\r\n";
        part += "\r\n" + content;
        part += "\r\n";
        return part;
    }
}

/* Format Example

--NG911klOamDldNie3i4M8kfxCWtVlk3IJp2saEIEOfiXcoaOeCb4hcf
MIME-Version: 1.0
Content-ID: <902143524985@10.0.8.1>
Content-Type: application/sdp
Content-Transfer-Encoding: 8bit

v=0
o=android2@64.131.109.30 0 0 IN IP4 10.0.8.1
s=Session SIP/SDP
c=IN IP4 10.0.8.1
t=0 0
m=audio 21000 RTP/AVP 8 0 101
a=rtpmap:8 PCMA/8000
a=rtpmap:0 PCMU/8000
a=rtpmap:101 telephone-event/8000
a=fmtp:101 0-15
m=video 21070 RTP/AVP 103
a=rtpmap:103 h263-1998/90000

*/
