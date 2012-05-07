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
name|jcr
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
name|commons
operator|.
name|SimpleValueFactory
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
name|ContentSession
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
name|ContentRepository
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
name|ContentRepositoryImpl
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
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|login
operator|.
name|LoginException
import|;
end_import

begin_comment
comment|/**  * {@code RepositoryImpl}...  */
end_comment

begin_class
specifier|public
class|class
name|RepositoryImpl
implements|implements
name|Repository
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
name|RepositoryImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Descriptors
name|descriptors
init|=
operator|new
name|Descriptors
argument_list|(
operator|new
name|SimpleValueFactory
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ContentRepository
name|contentRepository
decl_stmt|;
specifier|public
name|RepositoryImpl
parameter_list|(
name|ContentRepository
name|contentRepository
parameter_list|)
block|{
name|this
operator|.
name|contentRepository
operator|=
name|contentRepository
expr_stmt|;
block|}
comment|/**      * Utility constructor that creates a new in-memory repository for use      * mostly in test cases.      */
specifier|public
name|RepositoryImpl
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ContentRepositoryImpl
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//---------------------------------------------------------< Repository>---
comment|/**      * @see javax.jcr.Repository#getDescriptorKeys()      */
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getDescriptorKeys
parameter_list|()
block|{
return|return
name|descriptors
operator|.
name|getKeys
argument_list|()
return|;
block|}
comment|/**      * @see Repository#isStandardDescriptor(String)      */
annotation|@
name|Override
specifier|public
name|boolean
name|isStandardDescriptor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|descriptors
operator|.
name|isStandardDescriptor
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Repository#getDescriptor(String)      */
annotation|@
name|Override
specifier|public
name|String
name|getDescriptor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
try|try
block|{
name|Value
name|v
init|=
name|getDescriptorValue
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|v
operator|==
literal|null
condition|?
literal|null
else|:
name|v
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Error converting value for descriptor with key {} to string"
argument_list|,
name|key
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**      * @see javax.jcr.Repository#getDescriptorValue(String)      */
annotation|@
name|Override
specifier|public
name|Value
name|getDescriptorValue
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|descriptors
operator|.
name|getValue
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Repository#getDescriptorValues(String)      */
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getDescriptorValues
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|descriptors
operator|.
name|getValues
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Repository#isSingleValueDescriptor(String)      */
annotation|@
name|Override
specifier|public
name|boolean
name|isSingleValueDescriptor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|descriptors
operator|.
name|isSingleValueDescriptor
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Repository#login(javax.jcr.Credentials, String)      */
annotation|@
name|Override
specifier|public
name|Session
name|login
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|String
name|workspaceName
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// TODO: needs complete refactoring
try|try
block|{
name|ContentSession
name|contentSession
init|=
name|contentRepository
operator|.
name|login
argument_list|(
name|credentials
argument_list|,
name|workspaceName
argument_list|)
decl_stmt|;
return|return
operator|new
name|SessionDelegate
argument_list|(
name|this
argument_list|,
name|contentSession
argument_list|)
operator|.
name|getSession
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|javax
operator|.
name|jcr
operator|.
name|LoginException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Calls {@link Repository#login(Credentials, String)} with      * {@code null} arguments.      *      * @return logged in session      * @throws RepositoryException if an error occurs      */
annotation|@
name|Override
specifier|public
name|Session
name|login
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Calls {@link Repository#login(Credentials, String)} with      * the given credentials and a {@code null} workspace name.      *      * @param credentials login credentials      * @return logged in session      * @throws RepositoryException if an error occurs      */
annotation|@
name|Override
specifier|public
name|Session
name|login
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|login
argument_list|(
name|credentials
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Calls {@link Repository#login(Credentials, String)} with      * {@code null} credentials and the given workspace name.      *      * @param workspace workspace name      * @return logged in session      * @throws RepositoryException if an error occurs      */
annotation|@
name|Override
specifier|public
name|Session
name|login
parameter_list|(
name|String
name|workspace
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|login
argument_list|(
literal|null
argument_list|,
name|workspace
argument_list|)
return|;
block|}
block|}
end_class

end_unit

