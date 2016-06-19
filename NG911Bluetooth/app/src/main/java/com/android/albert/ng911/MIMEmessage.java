package com.android.albert.ng911;

import java.util.List;

public class MIMEmessage {

    String version;
    String contentType;
    String boundary;
    List<MIMEpart> parts;

    public MIMEmessage(String version, String contentType, String boundary, List<MIMEpart> parts) {
        this.version = version;
        this.contentType = contentType;
        this.boundary = boundary;
        this.parts = parts;
    }

    /**
     *
     * @param contentType
     * @param boundary
     * @param parts
     */
    public MIMEmessage(String contentType, String boundary, List<MIMEpart> parts) {
        this.version = "1.0";
        this.contentType = contentType;
        this.boundary = boundary;
        this.parts = parts;
    }

    @Override
    public String toString() {
        String mime = "";   //no need to introduce CRLF, it's introduced in the method 'setBody' of 'BaseMessage.java'

        for(MIMEpart part: parts) {
            mime += "--" + boundary;
            mime += part.toString();
        }

        mime += "\r\n--" + boundary + "--\r\n";//last boundary indicating end of MIME message
        return mime;
    }
}
