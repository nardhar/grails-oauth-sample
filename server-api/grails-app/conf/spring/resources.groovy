import server.api.CustomAuthenticationProvider
import server.api.marshallers.*
import grails.converters.JSON
import org.grails.web.converters.configuration.ObjectMarshallerRegisterer

beans = {
    personRenderer(PersonXmlRenderer)
    personJsonMarshaller(ObjectMarshallerRegisterer) {
        marshaller = new PersonMarshallerJson()
        converterClass = JSON
        priority = 1
    }
    customAuthenticationProvider(CustomAuthenticationProvider) {
        personCheckService = ref('personCheckService')
        grailsApplication = ref('grailsApplication')
    }
}