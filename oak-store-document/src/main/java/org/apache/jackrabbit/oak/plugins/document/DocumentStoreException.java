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
name|plugins
operator|.
name|document
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  *<code>DocumentStoreException</code> is a runtime exception for  * {@code DocumentStore} implementations to signal unexpected problems like  * a communication exception.  */
end_comment

begin_class
specifier|public
class|class
name|DocumentStoreException
extends|extends
name|RuntimeException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|634445274043721284L
decl_stmt|;
specifier|public
name|DocumentStoreException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DocumentStoreException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DocumentStoreException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|DocumentStoreException
name|convert
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
name|convert
argument_list|(
name|t
argument_list|,
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|DocumentStoreException
name|convert
parameter_list|(
name|Throwable
name|t
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|DocumentStoreException
condition|)
block|{
return|return
operator|(
name|DocumentStoreException
operator|)
name|t
return|;
block|}
else|else
block|{
return|return
operator|new
name|DocumentStoreException
argument_list|(
name|msg
argument_list|,
name|t
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|DocumentStoreException
name|convert
parameter_list|(
name|Throwable
name|t
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|)
block|{
name|String
name|msg
init|=
name|t
operator|.
name|getMessage
argument_list|()
operator|+
literal|" "
operator|+
name|Lists
operator|.
name|newArrayList
argument_list|(
name|ids
argument_list|)
decl_stmt|;
return|return
name|convert
argument_list|(
name|t
argument_list|,
name|msg
argument_list|)
return|;
block|}
block|}
end_class

end_unit

