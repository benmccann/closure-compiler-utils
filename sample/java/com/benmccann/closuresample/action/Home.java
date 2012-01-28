// Copyright 2012 Benjamin McCann. All Rights Reserved.

package com.benmccann.closuresample.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Ben McCann (benmccann.com)
 */
public class Home extends ActionSupport {
  
  private static final long serialVersionUID = 1L;

  @Action(
    value = "",
    results = {@Result(name="success", location="/index.html")}
  )
  public String execute() throws Exception {
    return SUCCESS;
  }

}
