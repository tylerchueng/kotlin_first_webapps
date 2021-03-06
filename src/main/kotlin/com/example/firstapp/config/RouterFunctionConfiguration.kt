package com.example.firstapp.config

import com.example.firstapp.blog.service.impl.ContentService
import com.example.firstapp.common.utils.PageUtils
import com.example.firstapp.common.utils.Query
import com.example.firstapp.domain.User
import com.example.firstapp.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


/**
 *  路由器函数配置
 */
@Configuration
class RouterFunctionConfiguration {

    @Bean
    fun personFindAll(userRepository: UserRepository): RouterFunction<ServerResponse> {
        return RouterFunctions.route( RequestPredicates.GET("/person/find/all"), HandlerFunction {
            val all = userRepository.findAll();
            var respone: Mono<ServerResponse>? = null;
            var userFlux: Flux<User> = Flux.fromIterable(all);
             ServerResponse.ok().body(userFlux, User::class.java);
        });

    }

    @Bean
    fun opentList(bContentService: ContentService): RouterFunction<ServerResponse> {
        return RouterFunctions.route( RequestPredicates.GET("/blog/open/list2"),
                HandlerFunction {

                    var type = it.queryParam("type");
                    var limit = it.queryParam("limit");
                    var offset = it.queryParam("offset");
                    var map = HashMap<String,String>();
                    map.put("type", type.get())
                    map.put("limit",limit.get())
                    map.put("offset",offset.get())
                    val query = Query(map)
                    val bContentList = bContentService?.list(query)
                    val total = bContentService?.count(query)
                    var pageUtils = PageUtils(bContentList, total!!)
                    var respone: Mono<PageUtils> = Mono.justOrEmpty(pageUtils);
                    ServerResponse.ok().body(respone, PageUtils::class.java);
        });

    }


}