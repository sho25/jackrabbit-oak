begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|json
operator|.
name|JsopTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A {@code NodeFilter} represents a filter on property and/or node names specified  * in JSON format. It allows to specify glob patterns for names of nodes and/or  * properties to be included or excluded.  *<p/>  * Example:  *<pre>  * {  *   "nodes": [ "foo*", "-foo1" ],  *   "properties": [ "*", "-:childNodeCount" ]  * }  *</pre>  *  * @see NameFilter  * @see org.apache.jackrabbit.mk.api.MicroKernel#getNodes(String, String, int, long, int, String)  */
end_comment

begin_class
specifier|public
class|class
name|NodeFilter
block|{
specifier|private
specifier|final
name|NameFilter
name|nodeFilter
decl_stmt|;
specifier|private
specifier|final
name|NameFilter
name|propFilter
decl_stmt|;
specifier|private
name|NodeFilter
parameter_list|(
name|NameFilter
name|nodeFilter
parameter_list|,
name|NameFilter
name|propFilter
parameter_list|)
block|{
name|this
operator|.
name|nodeFilter
operator|=
name|nodeFilter
expr_stmt|;
name|this
operator|.
name|propFilter
operator|=
name|propFilter
expr_stmt|;
block|}
specifier|public
specifier|static
name|NodeFilter
name|parse
parameter_list|(
name|String
name|json
parameter_list|)
block|{
comment|// parse json format filter
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|NameFilter
name|nodeFilter
init|=
literal|null
decl_stmt|,
name|propFilter
init|=
literal|null
decl_stmt|;
do|do
block|{
name|String
name|type
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
index|[]
name|globs
init|=
name|parseArray
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"nodes"
argument_list|)
condition|)
block|{
name|nodeFilter
operator|=
operator|new
name|NameFilter
argument_list|(
name|globs
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"properties"
argument_list|)
condition|)
block|{
name|propFilter
operator|=
operator|new
name|NameFilter
argument_list|(
name|globs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal filter format"
argument_list|)
throw|;
block|}
block|}
do|while
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|t
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
operator|new
name|NodeFilter
argument_list|(
name|nodeFilter
argument_list|,
name|propFilter
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
index|[]
name|parseArray
parameter_list|(
name|JsopTokenizer
name|t
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
do|do
block|{
name|l
operator|.
name|add
argument_list|(
name|t
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|t
operator|.
name|read
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
name|NameFilter
name|getChildNodeFilter
parameter_list|()
block|{
return|return
name|nodeFilter
return|;
block|}
specifier|public
name|NameFilter
name|getPropertyFilter
parameter_list|()
block|{
return|return
name|propFilter
return|;
block|}
specifier|public
name|boolean
name|includeNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|nodeFilter
operator|==
literal|null
operator|||
name|nodeFilter
operator|.
name|matches
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|includeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|propFilter
operator|==
literal|null
operator|||
name|propFilter
operator|.
name|matches
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

