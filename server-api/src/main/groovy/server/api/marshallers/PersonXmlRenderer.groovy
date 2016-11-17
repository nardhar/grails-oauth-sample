package server.api.marshallers

import grails.rest.render.*
import grails.web.mime.MimeType
import server.api.Person

class PersonXmlRenderer extends AbstractRenderer<Person> {
    PersonXmlRenderer() {
        super(Person, [MimeType.XML,MimeType.TEXT_XML] as MimeType[])
    }

    void render(Person object, RenderContext context) {
        context.contentType = MimeType.XML.name

        def xml = new groovy.xml.MarkupBuilder(context.writer)
        xml.person(username: object.username, fullName: object.fullName, email: object.email)
    }
}