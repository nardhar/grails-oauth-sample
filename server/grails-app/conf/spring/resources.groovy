//import bo.gob.ypfb.ldapSample.YpfbUserDetailsContextMapper
import nardhar.oauth.CustomAuthenticationProvider

beans = {
    //ldapUserDetailsMapper(YpfbUserDetailsContextMapper) {
    //}
    customAuthenticationProvider(CustomAuthenticationProvider) {
        ldapCheckService = ref('ldapCheckService')
    }
}