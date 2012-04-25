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
name|namepath
package|;
end_package

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractNameMapper
implements|implements
name|NameMapper
block|{
specifier|protected
specifier|abstract
name|String
name|getJcrPrefix
parameter_list|(
name|String
name|oakPrefix
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|String
name|getOakPrefix
parameter_list|(
name|String
name|jcrPrefix
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|String
name|getOakPrefixFromURI
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|String
name|getOakName
parameter_list|(
name|String
name|jcrName
parameter_list|)
block|{
name|int
name|pos
init|=
name|jcrName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
block|{
comment|// no colon
return|return
name|jcrName
return|;
block|}
else|else
block|{
if|if
condition|(
name|jcrName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'{'
condition|)
block|{
name|int
name|endpos
init|=
name|jcrName
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|)
decl_stmt|;
if|if
condition|(
name|endpos
operator|>
name|pos
condition|)
block|{
comment|// expanded name
name|String
name|nsuri
init|=
name|jcrName
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|endpos
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|jcrName
operator|.
name|substring
argument_list|(
name|endpos
operator|+
literal|1
argument_list|)
decl_stmt|;
name|String
name|oakPref
init|=
name|getOakPrefixFromURI
argument_list|(
name|nsuri
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPref
operator|==
literal|null
condition|)
block|{
comment|// TODO
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|oakPref
operator|+
literal|':'
operator|+
name|name
return|;
block|}
block|}
block|}
comment|// otherwise: not an expanded name
name|String
name|pref
init|=
name|jcrName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|jcrName
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
decl_stmt|;
name|String
name|oakPrefix
init|=
name|getOakPrefix
argument_list|(
name|pref
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPrefix
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"prefix '"
operator|+
name|pref
operator|+
literal|"' is not mapped"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|oakPrefix
operator|+
literal|':'
operator|+
name|name
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJcrName
parameter_list|(
name|String
name|oakName
parameter_list|)
block|{
name|int
name|pos
init|=
name|oakName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
block|{
comment|// non-prefixed
return|return
name|oakName
return|;
block|}
else|else
block|{
name|String
name|pref
init|=
name|oakName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|oakName
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|pref
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"invalid oak name (maybe expanded name leaked out?): "
operator|+
name|oakName
argument_list|)
throw|;
block|}
name|String
name|jcrPrefix
init|=
name|getJcrPrefix
argument_list|(
name|pref
argument_list|)
decl_stmt|;
if|if
condition|(
name|jcrPrefix
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"invalid oak name: "
operator|+
name|oakName
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|jcrPrefix
operator|+
literal|':'
operator|+
name|name
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

