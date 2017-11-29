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
name|security
operator|.
name|internal
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|security
operator|.
name|SecurityProviderImpl
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
name|spi
operator|.
name|security
operator|.
name|ConfigurationParameters
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
name|spi
operator|.
name|security
operator|.
name|SecurityProvider
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|SecurityProviderBuilder
block|{
specifier|private
name|ConfigurationParameters
name|configuration
init|=
literal|null
decl_stmt|;
specifier|public
name|SecurityProviderBuilder
name|with
parameter_list|(
annotation|@
name|Nonnull
name|ConfigurationParameters
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|configuration
operator|=
name|checkNotNull
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SecurityProvider
name|build
parameter_list|()
block|{
if|if
condition|(
name|configuration
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|SecurityProviderImpl
argument_list|(
name|configuration
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SecurityProviderImpl
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

