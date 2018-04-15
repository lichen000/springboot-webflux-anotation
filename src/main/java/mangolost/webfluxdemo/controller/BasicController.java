package mangolost.webfluxdemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/basic")
public class BasicController {

    /**
     *
     * @return
     */
    @RequestMapping("/hello")
    public Mono<String> sayHelloWorld() {
        return Mono.just("Hello World");
    }
}
