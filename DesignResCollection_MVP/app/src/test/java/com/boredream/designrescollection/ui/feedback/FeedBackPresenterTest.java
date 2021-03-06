package com.boredream.designrescollection.ui.feedback;

import com.boredream.bdcodehelper.BoreConstants;
import com.boredream.designrescollection.base.BaseEntity;
import com.boredream.designrescollection.entity.FeedBack;
import com.boredream.designrescollection.net.HttpRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 提交反馈很少有失败情况，所以使用mock模拟接口部分手动返回错误结果，以验证对应场景
 * 同时也提供真实接口的presenter，用于测试真实数据
 */
public class FeedBackPresenterTest {

    private FeedBackPresenter presenter;

    private FeedBackPresenter mockPresenter;

    @Mock
    private FeedBackContract.View view;

    @Mock
    private HttpRequest.ApiService api;

    @Before
    public void setupMocksAndView() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // The mockPresenter wont't update the view unless it's active.
        when(view.isActive()).thenReturn(true);

        BoreConstants.isUnitTest = true;

        presenter = new FeedBackPresenter(view, HttpRequest.getInstance().service);
        mockPresenter = new FeedBackPresenter(view, api);
    }

    @Test
    public void testAddFeedback_Success() throws Exception {
        String content = "fuck it !!!!!!!!!!!!!!";
        String email = "110@qq.com";
        presenter.addFeedback(content, email);

        verify(view).showProgress();
        verify(view).dismissProgress();
        verify(view).addFeedbackSuccess();
    }

    @Test
    public void testAddFeedback_Mock_Success() throws Exception {
        // 模拟数据，当api调用addFeedBack接口传入任意值时，就返回成功的基本数据BaseEntity
        when(api.addFeedBack(any(FeedBack.class))).thenReturn(Observable.just(new BaseEntity()));

        String content = "fuck it !!!!!!!!!!!!!!";
        String email = "110@qq.com";
        mockPresenter.addFeedback(content, email);

        verify(view).showProgress();
        verify(view).dismissProgress();
        verify(view).addFeedbackSuccess();
    }

    @Test
    public void testAddFeedback_Mock_Error() throws Exception {
        // 模拟数据，当api调用addFeedBack接口传入任意值时，就抛出错误error
        when(api.addFeedBack(any(FeedBack.class))).thenReturn(Observable.<BaseEntity>error(new Exception("blablabla")));

        String content = "fuck it !!!!!!!!!!!!!!";
        String email = "110@qq.com";
        mockPresenter.addFeedback(content, email);

        verify(view).showProgress();
        verify(view).dismissProgress();
        verify(view).showTip("反馈提交失败");
    }
}