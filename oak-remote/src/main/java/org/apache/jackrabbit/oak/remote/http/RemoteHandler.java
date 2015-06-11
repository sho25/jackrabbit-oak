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
name|oak
operator|.
name|remote
operator|.
name|http
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
name|remote
operator|.
name|http
operator|.
name|handler
operator|.
name|Handler
import|;
end_import

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
name|remote
operator|.
name|http
operator|.
name|matcher
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
class|class
name|RemoteHandler
implements|implements
name|Matcher
implements|,
name|Handler
block|{
specifier|private
name|Matcher
name|matcher
decl_stmt|;
specifier|private
name|Handler
name|handler
decl_stmt|;
specifier|public
name|RemoteHandler
parameter_list|(
name|Matcher
name|matcher
parameter_list|,
name|Handler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|matcher
operator|=
name|matcher
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|handler
operator|.
name|handle
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|match
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
return|return
name|matcher
operator|.
name|match
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
end_class

end_unit

