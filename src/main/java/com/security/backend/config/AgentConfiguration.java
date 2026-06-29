package com.security.backend.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.Agent;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {


    @Bean
    public Agent reactAgent() {
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey("sk-ws-H.RYIPXHP.RSVJ.MEUCIQCWlmnTTTnHfFCqvcaVL57SYgKQRrtRrwr31ev9qpJ7CAIgdI4oH8CXF0UPln55T8dNcCe2le0Lc3RJY4zFTt72diA")
                .build();

        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();

        ReactAgent agent = ReactAgent.builder()
                .name("weather_agent")
                .model(chatModel)
                .systemPrompt("You are a helpful assistant")
                .saver(new MemorySaver())
                .build();

        return agent;
    }
}
