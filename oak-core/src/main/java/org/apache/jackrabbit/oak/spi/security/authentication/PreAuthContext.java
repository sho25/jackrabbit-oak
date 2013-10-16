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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_comment
comment|/**  * LoginContext for pre-authenticated subjects that don't require further  * validation nor additional login/logout steps.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|PreAuthContext
implements|implements
name|LoginContext
block|{
specifier|private
specifier|final
name|Subject
name|subject
decl_stmt|;
specifier|public
name|PreAuthContext
parameter_list|(
name|Subject
name|subject
parameter_list|)
block|{
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Subject
name|getSubject
parameter_list|()
block|{
return|return
name|subject
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|login
parameter_list|()
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|logout
parameter_list|()
block|{
comment|// nothing to do
block|}
block|}
end_class

end_unit

