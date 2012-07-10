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
operator|.
name|nodetype
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
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|commons
operator|.
name|cnd
operator|.
name|CompactNodeTypeDefReader
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
name|commons
operator|.
name|cnd
operator|.
name|DefinitionBuilderFactory
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
name|commons
operator|.
name|cnd
operator|.
name|DefinitionBuilderFactory
operator|.
name|AbstractNodeDefinitionBuilder
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
name|commons
operator|.
name|cnd
operator|.
name|DefinitionBuilderFactory
operator|.
name|AbstractNodeTypeDefinitionBuilder
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
name|commons
operator|.
name|cnd
operator|.
name|DefinitionBuilderFactory
operator|.
name|AbstractPropertyDefinitionBuilder
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
name|commons
operator|.
name|cnd
operator|.
name|ParseException
import|;
end_import

begin_class
specifier|public
class|class
name|NodeTypeManagerDelegate
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|NodeTypeDelegate
argument_list|>
name|typeDelegates
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nsdefaults
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tmp
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|tmp
operator|.
name|put
argument_list|(
literal|"rep"
argument_list|,
literal|"internal"
argument_list|)
expr_stmt|;
comment|// TODO: https://issues.apache.org/jira/browse/OAK-74
name|tmp
operator|.
name|put
argument_list|(
literal|"jcr"
argument_list|,
literal|"http://www.jcp.org/jcr/1.0"
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|put
argument_list|(
literal|"nt"
argument_list|,
literal|"http://www.jcp.org/jcr/nt/1.0"
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|put
argument_list|(
literal|"mix"
argument_list|,
literal|"http://www.jcp.org/jcr/mix/1.0"
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|put
argument_list|(
literal|"xml"
argument_list|,
literal|"http://www.w3.org/XML/1998/namespace"
argument_list|)
expr_stmt|;
name|nsdefaults
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeTypeManagerDelegate
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|InputStream
name|stream
init|=
name|NodeTypeManagerImpl
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"builtin_nodetypes.cnd"
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
try|try
block|{
name|DefinitionBuilderFactory
argument_list|<
name|NodeTypeDelegate
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|dbf
init|=
operator|new
name|DefinitionDelegateBuilderFactory
argument_list|()
decl_stmt|;
name|CompactNodeTypeDefReader
argument_list|<
name|NodeTypeDelegate
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|cndr
init|=
operator|new
name|CompactNodeTypeDefReader
argument_list|<
name|NodeTypeDelegate
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|,
name|dbf
argument_list|)
decl_stmt|;
name|typeDelegates
operator|=
name|cndr
operator|.
name|getNodeTypeDefinitions
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Failed to load built-in node types"
argument_list|,
name|ex
argument_list|)
throw|;
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
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Failed to load built-in node types"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
specifier|public
name|List
argument_list|<
name|NodeTypeDelegate
argument_list|>
name|getAllNodeTypeDelegates
parameter_list|()
block|{
return|return
name|typeDelegates
return|;
block|}
specifier|private
class|class
name|DefinitionDelegateBuilderFactory
extends|extends
name|DefinitionBuilderFactory
argument_list|<
name|NodeTypeDelegate
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nsmap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getNamespaceMapping
parameter_list|()
block|{
return|return
name|nsmap
return|;
block|}
annotation|@
name|Override
specifier|public
name|AbstractNodeTypeDefinitionBuilder
argument_list|<
name|NodeTypeDelegate
argument_list|>
name|newNodeTypeDefinitionBuilder
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|NodeTypeDefinitionDelegateBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|t
init|=
name|nsdefaults
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Unsupported namespace prefix for initial CND load: "
operator|+
name|prefix
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|t
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Can't remap namespace prefix for initial CND laod: "
operator|+
name|prefix
argument_list|)
throw|;
block|}
name|nsmap
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNamespaceMapping
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nsmap
parameter_list|)
block|{
name|this
operator|.
name|nsmap
operator|=
name|nsmap
expr_stmt|;
block|}
specifier|public
name|String
name|convertNameToOak
parameter_list|(
name|String
name|cndName
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|cndName
return|;
comment|// is assumed to be the Oak name for now
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|convertNamesToOak
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|cndNames
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|cndName
range|:
name|cndNames
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|convertNameToOak
argument_list|(
name|cndName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
specifier|private
class|class
name|NodeTypeDefinitionDelegateBuilder
extends|extends
name|AbstractNodeTypeDefinitionBuilder
argument_list|<
name|NodeTypeDelegate
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|PropertyDefinitionDelegateBuilder
argument_list|>
name|propertyDefinitions
init|=
operator|new
name|ArrayList
argument_list|<
name|PropertyDefinitionDelegateBuilder
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|NodeDefinitionDelegateBuilder
argument_list|>
name|childNodeDefinitions
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeDefinitionDelegateBuilder
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|DefinitionDelegateBuilderFactory
name|ddbf
decl_stmt|;
specifier|private
name|String
name|primaryItemName
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|declaredSuperTypes
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|NodeTypeDefinitionDelegateBuilder
parameter_list|(
name|DefinitionDelegateBuilderFactory
name|ddbf
parameter_list|)
block|{
name|this
operator|.
name|ddbf
operator|=
name|ddbf
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addSupertype
parameter_list|(
name|String
name|superType
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|declaredSuperTypes
operator|.
name|add
argument_list|(
name|superType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPrimaryItemName
parameter_list|(
name|String
name|primaryItemName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|primaryItemName
operator|=
name|primaryItemName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|AbstractPropertyDefinitionBuilder
argument_list|<
name|NodeTypeDelegate
argument_list|>
name|newPropertyDefinitionBuilder
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|PropertyDefinitionDelegateBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AbstractNodeDefinitionBuilder
argument_list|<
name|NodeTypeDelegate
argument_list|>
name|newNodeDefinitionBuilder
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|NodeDefinitionDelegateBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeTypeDelegate
name|build
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|name
operator|=
name|ddbf
operator|.
name|convertNameToOak
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|declaredSuperTypes
operator|=
name|ddbf
operator|.
name|convertNamesToOak
argument_list|(
name|declaredSuperTypes
argument_list|)
expr_stmt|;
name|primaryItemName
operator|=
name|ddbf
operator|.
name|convertNameToOak
argument_list|(
name|primaryItemName
argument_list|)
expr_stmt|;
name|NodeTypeDelegate
name|result
init|=
operator|new
name|NodeTypeDelegate
argument_list|(
name|name
argument_list|,
name|declaredSuperTypes
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|declaredSuperTypes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|primaryItemName
argument_list|,
name|isMixin
argument_list|,
name|isAbstract
argument_list|,
name|isOrderable
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyDefinitionDelegateBuilder
name|pdb
range|:
name|propertyDefinitions
control|)
block|{
name|result
operator|.
name|addPropertyDefinitionDelegate
argument_list|(
name|pdb
operator|.
name|getPropertyDefinitionDelegate
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|NodeDefinitionDelegateBuilder
name|ndb
range|:
name|childNodeDefinitions
control|)
block|{
name|result
operator|.
name|addChildNodeDefinitionDelegate
argument_list|(
name|ndb
operator|.
name|getNodeDefinitionDelegate
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|addPropertyDefinition
parameter_list|(
name|PropertyDefinitionDelegateBuilder
name|pd
parameter_list|)
block|{
name|this
operator|.
name|propertyDefinitions
operator|.
name|add
argument_list|(
name|pd
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addNodeDefinition
parameter_list|(
name|NodeDefinitionDelegateBuilder
name|nd
parameter_list|)
block|{
name|this
operator|.
name|childNodeDefinitions
operator|.
name|add
argument_list|(
name|nd
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|convertNameToOak
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|ddbf
operator|.
name|convertNameToOak
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
specifier|private
class|class
name|NodeDefinitionDelegateBuilder
extends|extends
name|AbstractNodeDefinitionBuilder
argument_list|<
name|NodeTypeDelegate
argument_list|>
block|{
specifier|private
name|String
name|declaringNodeType
decl_stmt|;
specifier|private
name|String
name|defaultPrimaryType
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|requiredPrimaryTypes
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NodeTypeDefinitionDelegateBuilder
name|ndtb
decl_stmt|;
specifier|public
name|NodeDefinitionDelegateBuilder
parameter_list|(
name|NodeTypeDefinitionDelegateBuilder
name|ntdb
parameter_list|)
block|{
name|this
operator|.
name|ndtb
operator|=
name|ntdb
expr_stmt|;
block|}
specifier|public
name|NodeDefinitionDelegate
name|getNodeDefinitionDelegate
parameter_list|()
block|{
return|return
operator|new
name|NodeDefinitionDelegate
argument_list|(
name|name
argument_list|,
name|autocreate
argument_list|,
name|isMandatory
argument_list|,
name|onParent
argument_list|,
name|isProtected
argument_list|,
name|requiredPrimaryTypes
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|requiredPrimaryTypes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|defaultPrimaryType
argument_list|,
name|allowSns
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDefaultPrimaryType
parameter_list|(
name|String
name|defaultPrimaryType
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|defaultPrimaryType
operator|=
name|defaultPrimaryType
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addRequiredPrimaryType
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|requiredPrimaryTypes
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDeclaringNodeType
parameter_list|(
name|String
name|declaringNodeType
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|declaringNodeType
operator|=
name|declaringNodeType
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|build
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|ndtb
operator|.
name|addNodeDefinition
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|PropertyDefinitionDelegateBuilder
extends|extends
name|AbstractPropertyDefinitionBuilder
argument_list|<
name|NodeTypeDelegate
argument_list|>
block|{
specifier|private
name|String
name|declaringNodeType
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|defaultValues
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|valueConstraints
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NodeTypeDefinitionDelegateBuilder
name|ndtb
decl_stmt|;
specifier|public
name|PropertyDefinitionDelegateBuilder
parameter_list|(
name|NodeTypeDefinitionDelegateBuilder
name|ntdb
parameter_list|)
block|{
name|this
operator|.
name|ndtb
operator|=
name|ntdb
expr_stmt|;
block|}
specifier|public
name|PropertyDefinitionDelegate
name|getPropertyDefinitionDelegate
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|name
operator|=
name|ndtb
operator|.
name|convertNameToOak
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|PropertyDefinitionDelegate
argument_list|(
name|name
argument_list|,
name|autocreate
argument_list|,
name|isMandatory
argument_list|,
name|onParent
argument_list|,
name|isProtected
argument_list|,
name|requiredType
argument_list|,
name|isMultiple
argument_list|,
name|defaultValues
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addValueConstraint
parameter_list|(
name|String
name|constraint
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|valueConstraints
operator|.
name|add
argument_list|(
name|constraint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addDefaultValues
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|defaultValues
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDeclaringNodeType
parameter_list|(
name|String
name|declaringNodeType
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|declaringNodeType
operator|=
name|declaringNodeType
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|build
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|ndtb
operator|.
name|addPropertyDefinition
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

