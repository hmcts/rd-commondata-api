package uk.gov.hmcts.reform.cdapi.repository;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}
