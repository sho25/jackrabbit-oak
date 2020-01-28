begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|binary
package|;
end_package

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
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * Specifies the options to be used when requesting direct upload URIs via  * {@link org.apache.jackrabbit.api.JackrabbitValueFactory#initiateBinaryUpload(long, int, BinaryUploadOptions)}.  *<p>  * To specify upload options, obtain a {@link BinaryUploadOptionsBuilder}  * via the {@link #builder()} method, then specify the options desired and  * get the object via {@link BinaryUploadOptionsBuilder#build()}.  *<p>  * If no options are needed, use {@link BinaryUploadOptions#DEFAULT} which  * instructs the implementation to use the service provider default behavior.  */
end_comment

begin_class
annotation|@
name|ProviderType
specifier|public
specifier|final
class|class
name|BinaryUploadOptions
block|{
specifier|private
specifier|final
name|boolean
name|domainOverrideIgnore
decl_stmt|;
specifier|private
name|BinaryUploadOptions
parameter_list|(
name|boolean
name|domainOverrideIgnore
parameter_list|)
block|{
name|this
operator|.
name|domainOverrideIgnore
operator|=
name|domainOverrideIgnore
expr_stmt|;
block|}
comment|/**      * Provides a default instance of this class.  This instance enforces the      * proper default behaviors for the options.      */
specifier|public
specifier|static
specifier|final
name|BinaryUploadOptions
name|DEFAULT
init|=
name|BinaryUploadOptions
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/**      * Indicates whether the option to ignore any configured domain override      * setting has been specified.      *      * @return true if the domain override should be ignored; false otherwise.      * The default behavior is {@code false}, meaning that any configured domain      * override setting should be honored.      */
specifier|public
name|boolean
name|isDomainOverrideIgnored
parameter_list|()
block|{
return|return
name|domainOverrideIgnore
return|;
block|}
comment|/**      * Returns a {@link BinaryUploadOptionsBuilder} instance to be used for      * creating an instance of this class.      *      * @return A builder instance.      */
annotation|@
name|NotNull
specifier|public
specifier|static
name|BinaryUploadOptionsBuilder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|BinaryUploadOptionsBuilder
argument_list|()
return|;
block|}
comment|/**      * Used to build an instance of {@link BinaryUploadOptions} with the options      * set as desired by the caller.      */
specifier|public
specifier|static
specifier|final
class|class
name|BinaryUploadOptionsBuilder
block|{
specifier|private
name|boolean
name|domainOverrideIgnore
init|=
literal|false
decl_stmt|;
specifier|private
name|BinaryUploadOptionsBuilder
parameter_list|()
block|{ }
comment|/**          * Sets the option to ignore any configured domain override setting.          *          * The default value of this option is false, meaning that any          * configured domain override setting should be honored when generating          * signed upload URIs.  Setting this value to true will indicate that          * the signed upload URIs being generated should not honor any          * configured domain override setting.          *          * @param domainOverrideIgnore true to ignore any configured domain          *                             override setting, false otherwise.          * @return the calling instance.          */
specifier|public
name|BinaryUploadOptionsBuilder
name|withDomainOverrideIgnore
parameter_list|(
name|boolean
name|domainOverrideIgnore
parameter_list|)
block|{
name|this
operator|.
name|domainOverrideIgnore
operator|=
name|domainOverrideIgnore
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Construct a {@link BinaryUploadOptions} instance with the          * properties specified to the builder.          *          * @return A new {@link BinaryUploadOptions} instance built with the          *         properties specified to the builder.          */
specifier|public
name|BinaryUploadOptions
name|build
parameter_list|()
block|{
return|return
operator|new
name|BinaryUploadOptions
argument_list|(
name|domainOverrideIgnore
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
