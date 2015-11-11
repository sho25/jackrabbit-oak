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
name|authorization
operator|.
name|cug
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|annotation
operator|.
name|Nullable
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
name|Root
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
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
name|plugins
operator|.
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|plugins
operator|.
name|nodetype
operator|.
name|write
operator|.
name|NodeTypeRegistry
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
name|xml
operator|.
name|ImportBehavior
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
name|xml
operator|.
name|ProtectedItemImporter
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
name|TreeUtil
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
name|util
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * Utility methods for this CUG implementation package.  */
end_comment

begin_class
specifier|final
class|class
name|CugUtil
implements|implements
name|CugConstants
block|{
specifier|private
name|CugUtil
parameter_list|()
block|{}
specifier|public
specifier|static
name|boolean
name|hasCug
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|tree
operator|.
name|exists
argument_list|()
operator|&&
name|tree
operator|.
name|hasChild
argument_list|(
name|REP_CUG_POLICY
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|Tree
name|getCug
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
name|Tree
name|cugTree
init|=
operator|(
name|CugUtil
operator|.
name|hasCug
argument_list|(
name|tree
argument_list|)
operator|)
condition|?
name|tree
operator|.
name|getChild
argument_list|(
name|REP_CUG_POLICY
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|cugTree
operator|!=
literal|null
operator|&&
name|NT_REP_CUG_POLICY
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|cugTree
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|cugTree
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|definesCug
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|tree
operator|.
name|exists
argument_list|()
operator|&&
name|REP_CUG_POLICY
operator|.
name|equals
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|NT_REP_CUG_POLICY
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|definesCug
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|REP_PRINCIPAL_NAMES
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|definesCug
argument_list|(
name|tree
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isSupportedPath
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|ConfigurationParameters
name|config
parameter_list|)
block|{
if|if
condition|(
name|oakPath
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
for|for
control|(
name|String
name|supportedPath
range|:
name|config
operator|.
name|getConfigValue
argument_list|(
name|CugConfiguration
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
control|)
block|{
if|if
condition|(
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|supportedPath
argument_list|,
name|oakPath
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
specifier|static
name|int
name|getImportBehavior
parameter_list|(
name|ConfigurationParameters
name|config
parameter_list|)
block|{
name|String
name|importBehaviorStr
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
argument_list|,
name|ImportBehavior
operator|.
name|NAME_ABORT
argument_list|)
decl_stmt|;
return|return
name|ImportBehavior
operator|.
name|valueFromString
argument_list|(
name|importBehaviorStr
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|registerCugNodeTypes
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Root
name|root
parameter_list|)
block|{
try|try
block|{
name|ReadOnlyNodeTypeManager
name|ntMgr
init|=
operator|new
name|ReadOnlyNodeTypeManager
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Tree
name|getTypes
parameter_list|()
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|)
return|;
block|}
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|ntMgr
operator|.
name|hasNodeType
argument_list|(
name|NT_REP_CUG_POLICY
argument_list|)
condition|)
block|{
name|InputStream
name|stream
init|=
name|CugConfiguration
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"cug_nodetypes.cnd"
argument_list|)
decl_stmt|;
try|try
block|{
name|NodeTypeRegistry
operator|.
name|register
argument_list|(
name|root
argument_list|,
name|stream
argument_list|,
literal|"cug node types"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to read cug node types"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to read cug node types"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

