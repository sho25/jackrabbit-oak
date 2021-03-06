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
name|external
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_class
specifier|final
class|class
name|PreAuthCredentials
implements|implements
name|Credentials
block|{
specifier|static
specifier|final
name|String
name|PRE_AUTH_DONE
init|=
literal|"pre_auth_done"
decl_stmt|;
specifier|static
specifier|final
name|String
name|PRE_AUTH_FAIL
init|=
literal|"pre_auth_fail"
decl_stmt|;
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
specifier|private
name|String
name|msg
decl_stmt|;
name|PreAuthCredentials
parameter_list|(
annotation|@
name|Nullable
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
annotation|@
name|Nullable
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|userId
return|;
block|}
annotation|@
name|Nullable
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|msg
return|;
block|}
name|void
name|setMessage
parameter_list|(
annotation|@
name|NotNull
name|String
name|message
parameter_list|)
block|{
name|msg
operator|=
name|message
expr_stmt|;
block|}
block|}
end_class

end_unit

