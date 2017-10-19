begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|credentials
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCredentials
implements|implements
name|Credentials
block|{
specifier|protected
specifier|final
name|Map
name|attributes
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|String
name|userId
decl_stmt|;
specifier|public
name|AbstractCredentials
parameter_list|(
name|String
name|userId
parameter_list|)
block|{
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
block|}
comment|/**      * Returns the userId.      *      * @return the userId.      */
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|userId
return|;
block|}
comment|/**      * Stores an attribute in this credentials instance.      *      * @param name      *            a<code>String</code> specifying the name of the attribute      * @param value      *            the<code>Object</code> to be stored      */
specifier|public
name|void
name|setAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
comment|// name cannot be null
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
block|}
comment|// null value is the same as removeAttribute()
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|removeAttribute
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return;
block|}
synchronized|synchronized
init|(
name|attributes
init|)
block|{
name|attributes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the value of the named attribute as an<code>Object</code>, or      *<code>null</code> if no attribute of the given name exists.      *      * @param name      *            a<code>String</code> specifying the name of the attribute      * @return an<code>Object</code> containing the value of the attribute, or      *<code>null</code> if the attribute does not exist      */
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|attributes
init|)
block|{
return|return
operator|(
name|attributes
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|)
return|;
block|}
block|}
comment|/**      * Removes an attribute from this credentials instance.      *      * @param name      *            a<code>String</code> specifying the name of the attribute to      *            remove      */
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|attributes
init|)
block|{
name|attributes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return the attributes available to this credentials instance      */
specifier|public
name|Map
name|getAttributes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|attributes
argument_list|)
return|;
block|}
comment|/**      * Stores the attributes in this credentials instance.      *      * @param attributes The attributes to be stored      */
specifier|public
name|void
name|setAttributes
parameter_list|(
name|Map
name|attributes
parameter_list|)
block|{
synchronized|synchronized
init|(
name|attributes
init|)
block|{
name|this
operator|.
name|attributes
operator|.
name|putAll
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
