package nardhar.oauth

class AuthorizationCode {

    byte[] authentication
    String code

    static constraints = {
        code nullable: false, blank: false, unique: true
        authentication nullable: false, minSize: 1, maxSize: 1024 * 4
    }

    static mapping = {
        version false
        table name: 'authorization_code', schema: 'public'
	    id generator: 'sequence', params: [sequence: 'public.sq_authorization_code_id']
    }
}
