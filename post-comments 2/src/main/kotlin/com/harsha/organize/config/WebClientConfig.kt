package com.harsha.organize.config

import com.harsha.organize.dto.PostData
import io.netty.handler.ssl.SslContextBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Configuration
class WebClientConfig {

    @Bean
    fun getWebClient(): WebClient {
        // Create SSL context to trust all certificates
        val sslContext = SSLContext.getInstance("TLS")

        // Define trust managers to accept all certificates
        val trustManagers = arrayOf<TrustManager>(object : X509TrustManager {
            // Method to check client's trust - accepting all certificates
            override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {}

            // Method to check server's trust - accepting all certificates
            override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {}

            // Method to get accepted issuers - returning an empty array
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        // Initialize SSL context with the defined trust managers
        sslContext.init(null, trustManagers, null)

        // Create a custom HttpClient
        val httpClient = HttpClient.create()
            .secure { sslProvider ->
                sslProvider.sslContext(
                    SslContextBuilder.forClient().trustManager(
                        trustManagers[0] as X509TrustManager
                    ).build()
                )
            }

        // Create a custom ClientHttpConnector using the HttpClient
        val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient)

        // Create a WebClient with a custom connector and exchange strategies
        return WebClient.builder()
            .clientConnector(connector)
            .exchangeStrategies(ExchangeStrategies.builder().codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
            }.build())
            .build()
    }
}
