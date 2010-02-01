import org.codehaus.groovy.grails.commons.ConfigurationHolder

class WeceemPluginUrlMappings {
    static CONTENT_PREFIX = ((ConfigurationHolder.config.weceem.content.prefix instanceof String) ? 
        ConfigurationHolder.config.weceem.content.prefix : '')
    static TOOLS_PREFIX = ((ConfigurationHolder.config.weceem.tools.prefix instanceof String) ? 
        ConfigurationHolder.config.weceem.tools.prefix : 'wcm')
    static ADMIN_PREFIX = ((ConfigurationHolder.config.weceem.admin.prefix instanceof String) ?
        ConfigurationHolder.config.weceem.admin.prefix : 'wcm/admin')
    
    static FORBIDDEN_SPACE_URIS = [
        // Internal/app resources
        "_weceem/",
        "plugins/",
        "WEB-INF/",
        "fck",
        "WeceemFiles/",
        // Admin links
        "${WeceemPluginUrlMappings.ADMIN_PREFIX}/",
        "${WeceemPluginUrlMappings.TOOLS_PREFIX}/"
    ]
    
    static mappings = {

        def adminURI = "/${WeceemPluginUrlMappings.ADMIN_PREFIX}"
        
        delegate.(adminURI+"/$action?")(controller: 'portal')

        delegate.(adminURI+"/view/$action?/$id?")(controller: 'preview')
                
        delegate.(adminURI+"/repository/$action?")(controller: 'repository')

        delegate.(adminURI+"/repository/preview/$id")(controller: 'repository', action:'preview')

        delegate.(adminURI+"/repository/$space/$action?/$id?")(controller: 'repository')
                
        delegate.(adminURI+"/editor/$action?/$id?")(controller: 'editor')
        
        delegate.(adminURI+"/administration/synchronization/$action?/$id?")(controller: 'synchronization')

        delegate.(adminURI+"/versions/$action?/$id?")(controller: 'version')

        delegate.(adminURI+"/space/$action?/$id?")(controller: 'space')

        def toolFunctionsPrefix = (WeceemPluginUrlMappings.TOOLS_PREFIX ? '/' : '')+"${WeceemPluginUrlMappings.TOOLS_PREFIX}"

        name contentSubmission: delegate.(toolFunctionsPrefix+"/submit/$action?") {
            controller = "contentSubmission"
        }
        
        name feed: delegate.(toolFunctionsPrefix+"/feed/$action/$uri**") {
            controller = "feeds"
        }
        
        name archive: delegate.(toolFunctionsPrefix+"/archive/$uri**") {
            controller = "archive"
            action = "list"
        }
        
        // This is tricky
        def contentURI = (WeceemPluginUrlMappings.CONTENT_PREFIX ? '/' : '')+"${WeceemPluginUrlMappings.CONTENT_PREFIX}/$uri**"
        
        invokeMethod(contentURI, {
            controller = "content"
            action = "show"
            constraints {
                // @todo this is very ugly, clean up
                uri(validator: { v ->
                    !WeceemPluginUrlMappings.FORBIDDEN_SPACE_URIS.find { pref -> 
                        return v?.startsWith(pref) 
                    }
                })
            }
        })
        
        def rootURI = "/${WeceemPluginUrlMappings.CONTENT_PREFIX}"
        invokeMethod(rootURI, {
            controller = "content"
            action = "show"
        })

        "403"(view:'/denied')
        "500"(view:'/error')
    }
}
