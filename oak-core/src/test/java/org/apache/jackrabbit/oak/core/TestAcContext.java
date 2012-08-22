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
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|api
operator|.
name|CoreValueFactory
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
name|api
operator|.
name|PropertyState
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
name|api
operator|.
name|Tree
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
name|DefaultValidatorProvider
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
name|security
operator|.
name|authorization
operator|.
name|CompiledPermissions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Dummy implementation that omits any permission checks  */
end_comment

begin_class
specifier|public
class|class
name|TestAcContext
implements|implements
name|AccessControlContext
block|{
comment|/**      * logger instance      */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestAcContext
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
comment|// nop
block|}
annotation|@
name|Override
specifier|public
name|CompiledPermissions
name|getPermissions
parameter_list|()
block|{
return|return
operator|new
name|CompiledPermissions
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|canRead
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|isProperty
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|int
name|permissions
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|int
name|permissions
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|int
name|permissions
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValidatorProvider
name|getPermissionValidatorProvider
parameter_list|(
name|CoreValueFactory
name|valueFactory
parameter_list|)
block|{
return|return
operator|new
name|DefaultValidatorProvider
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValidatorProvider
name|getAccessControlValidatorProvider
parameter_list|(
name|CoreValueFactory
name|valueFactory
parameter_list|)
block|{
return|return
operator|new
name|DefaultValidatorProvider
argument_list|()
return|;
block|}
block|}
end_class

end_unit

