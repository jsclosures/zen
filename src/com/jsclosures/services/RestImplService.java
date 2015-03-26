package com.jsclosures.services;

import com.jsclosures.DataBean;
import com.jsclosures.RestService;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * implement a get, post (create), put (update) and delete
 *
 */
public interface RestImplService {
    public DataBean getData(RestService context, HttpServletRequest req,DataBean args);
    public DataBean postData(RestService context, HttpServletRequest req,DataBean args);
    public DataBean putData(RestService context, HttpServletRequest req,DataBean args);
    public DataBean deleteData(RestService context, HttpServletRequest req,DataBean args);
}
