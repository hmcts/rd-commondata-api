package uk.gov.hmcts.reform.cdapi.repository;

import com.github.benmanes.caffeine.cache.Cache;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import uk.gov.hmcts.reform.cdapi.exception.UnauthorizedException;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdamRepositoryTest {

    @Mock
    private IdamClient idamClient;

    @Spy
    private CacheManager cacheManager;

    @InjectMocks
    private IdamRepository idamRepository;

    @Test
    @SuppressWarnings("unchecked")
    void test_getUserInfo() {
        UserInfo userInfo = mock(UserInfo.class);
        CaffeineCache caffeineCacheMock = mock(CaffeineCache.class);
        Cache cache = spy(Cache.class);

        doReturn(userInfo).when(idamClient).getUserInfo(anyString());
        doReturn(caffeineCacheMock).when(cacheManager).getCache(anyString());

        doReturn(cache).when(caffeineCacheMock).getNativeCache();
        //doReturn(anyLong()).when(cache).estimatedSize();

        UserInfo returnedUserInfo = idamRepository.getUserInfo("Test");

        assertNotNull(returnedUserInfo);
        verify(idamClient, times(1)).getUserInfo(any());
        verify(cacheManager, times(1)).getCache(any());
        verify(caffeineCacheMock, times(1)).getNativeCache();
        verify(cache, times(1)).estimatedSize();
    }

    @Test
    void test_getUserInfo_unAuthorizedException() {

        UserInfo userInfo = mock(UserInfo.class);
        CaffeineCache caffeineCacheMock = mock(CaffeineCache.class);
        Cache cache = mock(Cache.class);

        Request request = Request.create(Request.HttpMethod.GET, "url",
                                         new HashMap<>(), null, new RequestTemplate());
        when(idamClient.getUserInfo(anyString()))
            .thenThrow(new FeignException
                .Unauthorized("User is not authorized", request,null));

        when(cacheManager.getCache(anyString())).thenReturn(caffeineCacheMock);
        doReturn(cache).when(caffeineCacheMock).getNativeCache();
        when(cache.estimatedSize()).thenReturn(anyLong());

        UnauthorizedException thrown = Assertions.assertThrows(UnauthorizedException.class, () -> {
            UserInfo returnedUserInfo = idamRepository.getUserInfo("Test");
        });


        assertThat(thrown.getMessage()).contains("User is not authorized");


    }
}
