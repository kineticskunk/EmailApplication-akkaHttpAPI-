
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Server extends AllDirectives {

    private final EmailRoutes emailRoutes;

public server (ActorSystem system, ActorRef emailActor) {

    emailRoutes = new EmailRoutes(system, emailActor);

}

    public static void main(String[] args) throws Exception {

        ActorSystem system = ActorSystem.create("EmailServer");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        ActorRef emailActor = system.actorOf(EmailActor.props(), "emailActor");

        Server app = new Server(system, emailActor);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8080), materializer);

        System.out.println("Server online at http://localhost:8080/");
        //#http-server
    }
    protected Route createRoute() {

        return emailRoutes.routes();
    }




    }

}