package controllers;

import play.GlobalSettings;
import play.Logger;



public class Global extends GlobalSettings {
	public void onStart(Application app) {
        Logger.info("Application has started");
    }

    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }
    
    
//    public Promise<SimpleResult> onError(RequestHeader request, Throwable t) {
//        return Promise.<SimpleResult>pure((SimpleResult)internalServerError(
//        	errorPage.render(t)
//        ));
//    }
    
//    public Promise<SimpleResult> onHandlerNotFound(RequestHeader request) {
//        return Promise<SimpleResult>.pure((SimpleResult)notFound(
//            views.html.notFound.render(request.uri())
//        ));
//    }
    
//    public Promise<SimpleResult> onBadRequest(RequestHeader request, String error) {
//        return Promise.<SimpleResult>.pure(badRequest("Don't try to hack the URI!"));
//    }
    
}
