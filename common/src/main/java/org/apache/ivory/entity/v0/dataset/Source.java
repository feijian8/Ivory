/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.apache.ivory.entity.v0.dataset;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Source")
public class Source {

  @XmlAttribute(required = true)
  protected String name;

  @XmlAttribute
  protected String bcp;

  @XmlAttribute(name = "use-bcp")
  protected Boolean useBcp;

  @XmlAttribute(required = true)
  protected Integer sla;

  public String getName() {
    return this.name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getBcp() {
    return this.bcp;
  }

  public void setBcp(String value) {
    this.bcp = value;
  }

  public Boolean isUseBcp() {
    return this.useBcp;
  }

  public void setUseBcp(Boolean value) {
    this.useBcp = value;
  }

  public Integer getSla() {
    return this.sla;
  }

  public void setSla(Integer value) {
    this.sla = value;
  }

}