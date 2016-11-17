package nardhar.oauth

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User implements Serializable {

   private static final long serialVersionUID = 1

   transient springSecurityService

   String username
   String password
   String fullname // el nombre completo del usuario
   //String email // los usuarios personas siempre tendrian un email
   boolean enabled = true
   boolean accountExpired
   boolean accountLocked
   boolean passwordExpired

   User(String username, String password, String fullname) {
      this()
      this.username = username
      this.password = password
      this.fullname = fullname
   }

   Set<Role> getAuthorities() {
      UserRole.findAllByUser(this)*.role
   }

   def beforeInsert() {
      encodePassword()
   }

   def beforeUpdate() {
      if (isDirty('password')) {
         encodePassword()
      }
   }

   protected void encodePassword() {
      password = springSecurityService?.passwordEncoder ?
         springSecurityService.encodePassword(password) :
         password
   }

   static transients = ['springSecurityService']

   static constraints = {
      username blank: false, unique: true
      password blank: false
   }

   static mapping = {
        table name: 'user', schema: 'public'
        //id generator: 'org.ypfb.util.PrimaryKeyGenerator', type: 'string', column: 'id'
        password column: '`password`'
        id generator: 'sequence', params: [sequence: 'public.sq_user_id']
   }

   def getEmail() {
       username + (username.contains('@') ? '' : '@ypfb.gob.bo') 
   }
   
}