package server.api

class UrlMappings {

    static mappings = {

        '/profile'(controller: 'profile') {
            action = [GET: 'index']
            format = 'json'
        }

        '/login'(controller: 'register', action: 'login')
        '/logout'(controller: 'register', action: 'logout')

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
