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
name|namepath
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_comment
comment|/**  * The {@code NamePathMapper} interface combines {@code NameMapper} and  * {@code PathMapper}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NamePathMapper
extends|extends
name|NameMapper
extends|,
name|PathMapper
block|{
specifier|public
name|NamePathMapper
name|DEFAULT
init|=
operator|new
name|Default
argument_list|()
decl_stmt|;
comment|/**      * Default implementation that doesn't perform any conversions for cases      * where a mapper object only deals with oak internal names and paths.      */
specifier|public
class|class
name|Default
implements|implements
name|NamePathMapper
block|{
annotation|@
name|Override
specifier|public
name|String
name|getOakNameOrNull
parameter_list|(
name|String
name|jcrName
parameter_list|)
block|{
return|return
name|jcrName
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getOakName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jcrName
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|jcrName
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSessionLocalMappings
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJcrName
parameter_list|(
name|String
name|oakName
parameter_list|)
block|{
return|return
name|oakName
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
name|jcrPath
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOakPathKeepIndex
parameter_list|(
name|String
name|jcrPath
parameter_list|)
block|{
return|return
name|jcrPath
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getJcrPath
parameter_list|(
name|String
name|oakPath
parameter_list|)
block|{
return|return
name|oakPath
return|;
block|}
block|}
block|}
end_interface

end_unit

