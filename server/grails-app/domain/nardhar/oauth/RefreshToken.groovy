package nardhar.oauth

class RefreshToken {

    String value
    Date expiration
    byte[] authentication

    static constraints = {
        value nullable: false, blank: false, unique: true
        expiration nullable: true
        authentication nullable: false, minSize: 1, maxSize: 1024 * 4
    }

    static mapping = {
        version false
        table name: 'refresh_token', schema: 'public'
	    id generator: 'sequence', params: [sequence: 'public.sq_refresh_token_id']
    }
}
