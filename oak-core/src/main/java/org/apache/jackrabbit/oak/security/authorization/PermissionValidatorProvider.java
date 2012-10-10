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
name|authorization
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|core
operator|.
name|ReadOnlyTree
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
name|commit
operator|.
name|Validator
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
name|commit
operator|.
name|ValidatorProvider
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
name|authorization
operator|.
name|AccessControlContext
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
name|state
operator|.
name|NodeState
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
name|util
operator|.
name|NodeUtil
import|;
end_import

begin_comment
comment|/**  * PermissionValidatorProvider... TODO  */
end_comment

begin_class
specifier|public
class|class
name|PermissionValidatorProvider
implements|implements
name|ValidatorProvider
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|Subject
name|subject
init|=
name|Subject
operator|.
name|getSubject
argument_list|(
name|AccessController
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|subject
operator|==
literal|null
condition|)
block|{
comment|// use empty subject
name|subject
operator|=
operator|new
name|Subject
argument_list|()
expr_stmt|;
block|}
comment|// FIXME: should use same provider as in ContentRepositoryImpl
name|AccessControlContext
name|context
init|=
operator|new
name|AccessControlProviderImpl
argument_list|()
operator|.
name|getAccessControlContext
argument_list|(
name|subject
argument_list|)
decl_stmt|;
name|NodeUtil
name|rootBefore
init|=
operator|new
name|NodeUtil
argument_list|(
operator|new
name|ReadOnlyTree
argument_list|(
name|before
argument_list|)
argument_list|)
decl_stmt|;
name|NodeUtil
name|rootAfter
init|=
operator|new
name|NodeUtil
argument_list|(
operator|new
name|ReadOnlyTree
argument_list|(
name|after
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|PermissionValidator
argument_list|(
name|context
operator|.
name|getPermissions
argument_list|()
argument_list|,
name|rootBefore
argument_list|,
name|rootAfter
argument_list|)
return|;
block|}
block|}
end_class

end_unit

