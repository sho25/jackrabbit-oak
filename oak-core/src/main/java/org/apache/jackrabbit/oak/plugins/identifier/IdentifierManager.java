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
name|plugins
operator|.
name|identifier
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|UUID
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
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
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
name|JcrConstants
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
name|PropertyValue
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
name|QueryEngine
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
name|Result
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
name|ResultRow
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
name|api
operator|.
name|Type
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
name|commons
operator|.
name|PathUtils
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
name|namepath
operator|.
name|NamePathMapper
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
name|memory
operator|.
name|StringPropertyState
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
name|version
operator|.
name|VersionConstants
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
name|query
operator|.
name|PropertyValues
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
name|checkArgument
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
name|Predicates
operator|.
name|notNull
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
name|collect
operator|.
name|Iterators
operator|.
name|emptyIterator
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
name|collect
operator|.
name|Iterators
operator|.
name|filter
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
name|collect
operator|.
name|Iterators
operator|.
name|singletonIterator
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
name|collect
operator|.
name|Iterators
operator|.
name|transform
import|;
end_import

begin_import
import|import static
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
name|QueryEngine
operator|.
name|NO_MAPPINGS
import|;
end_import

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
specifier|public
class|class
name|IdentifierManager
block|{
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
name|IdentifierManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|ReadOnlyNodeTypeManager
name|nodeTypeManager
decl_stmt|;
specifier|public
name|IdentifierManager
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|nodeTypeManager
operator|=
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
name|String
name|generateUUID
parameter_list|()
block|{
return|return
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|static
name|String
name|generateUUID
parameter_list|(
name|String
name|hint
parameter_list|)
block|{
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|nameUUIDFromBytes
argument_list|(
name|hint
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|uuid
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isValidUUID
parameter_list|(
name|String
name|uuid
parameter_list|)
block|{
try|try
block|{
name|UUID
operator|.
name|fromString
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Return the identifier of a tree.      *      * @param tree  a tree      * @return  identifier of {@code tree}      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|String
name|getIdentifier
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|tree
operator|.
name|isRoot
argument_list|()
condition|)
block|{
return|return
literal|"/"
return|;
block|}
else|else
block|{
name|String
name|parentId
init|=
name|getIdentifier
argument_list|(
name|tree
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentId
argument_list|,
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * The possibly non existing tree identified by the specified {@code identifier} or {@code null}.      *      * @param identifier The identifier of the tree such as exposed by {@link #getIdentifier(Tree)}      * @return The tree with the given {@code identifier} or {@code null} if no      *         such tree exists.      */
annotation|@
name|CheckForNull
specifier|public
name|Tree
name|getTree
parameter_list|(
name|String
name|identifier
parameter_list|)
block|{
if|if
condition|(
name|identifier
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|identifier
argument_list|)
return|;
block|}
else|else
block|{
name|int
name|k
init|=
name|identifier
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|uuid
init|=
name|k
operator|==
operator|-
literal|1
condition|?
name|identifier
else|:
name|identifier
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|k
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|isValidUUID
argument_list|(
name|uuid
argument_list|)
argument_list|,
literal|"Not a valid identifier '"
operator|+
name|identifier
operator|+
literal|'\''
argument_list|)
expr_stmt|;
name|String
name|basePath
init|=
name|resolveUUID
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|basePath
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|k
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|basePath
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|basePath
argument_list|,
name|identifier
operator|.
name|substring
argument_list|(
name|k
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|/**      * The path of the tree identified by the specified {@code identifier} or {@code null}.      *      * @param identifier The identifier of the tree such as exposed by {@link #getIdentifier(Tree)}      * @return The path of the tree with the given {@code identifier} or {@code null} if no      *         such tree exists or if the tree is not accessible.      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getPath
parameter_list|(
name|String
name|identifier
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
return|return
name|tree
operator|!=
literal|null
operator|&&
name|tree
operator|.
name|exists
argument_list|()
condition|?
name|tree
operator|.
name|getPath
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**      * Returns the path of the tree references by the specified (weak)      * reference {@code PropertyState}.      *      * @param referenceValue A (weak) reference value.      * @return The tree with the given {@code identifier} or {@code null} if no      *         such tree exists or isn't accessible to the content session.      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getPath
parameter_list|(
name|PropertyState
name|referenceValue
parameter_list|)
block|{
name|int
name|type
init|=
name|referenceValue
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|REFERENCE
operator|||
name|type
operator|==
name|PropertyType
operator|.
name|WEAKREFERENCE
condition|)
block|{
return|return
name|resolveUUID
argument_list|(
name|referenceValue
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid value type"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the path of the tree references by the specified (weak)      * reference {@code PropertyState}.      *      * @param referenceValue A (weak) reference value.      * @return The tree with the given {@code identifier} or {@code null} if no      *         such tree exists or isn't accessible to the content session.      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getPath
parameter_list|(
name|PropertyValue
name|referenceValue
parameter_list|)
block|{
name|int
name|type
init|=
name|referenceValue
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|REFERENCE
operator|||
name|type
operator|==
name|PropertyType
operator|.
name|WEAKREFERENCE
condition|)
block|{
return|return
name|resolveUUID
argument_list|(
name|referenceValue
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid value type"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Searches all reference properties to the specified {@code tree} that match      * the given name and node type constraints.      *      * @param weak          if {@code true} only weak references are returned. Otherwise only      *                      hard references are returned.      * @param tree          The tree for which references should be searched.      * @param propertyName  A name constraint for the reference properties;      *                      {@code null} if no constraint should be enforced.      * @param nodeTypeNames Node type constraints to be enforced when using      *                      for reference properties; the specified names are expected to be internal      *                      oak names.      * @return A set of oak paths of those reference properties referring to the      *         specified {@code tree} and matching the constraints.      */
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getReferences
parameter_list|(
name|boolean
name|weak
parameter_list|,
name|Tree
name|tree
parameter_list|,
specifier|final
name|String
name|propertyName
parameter_list|,
specifier|final
name|String
modifier|...
name|nodeTypeNames
parameter_list|)
block|{
if|if
condition|(
operator|!
name|nodeTypeManager
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
comment|// shortcut
block|}
specifier|final
name|String
name|uuid
init|=
name|getIdentifier
argument_list|(
name|tree
argument_list|)
decl_stmt|;
name|String
name|reference
init|=
name|weak
condition|?
name|PropertyType
operator|.
name|TYPENAME_WEAKREFERENCE
else|:
name|PropertyType
operator|.
name|TYPENAME_REFERENCE
decl_stmt|;
name|String
name|pName
init|=
name|propertyName
operator|==
literal|null
condition|?
literal|"*"
else|:
name|propertyName
decl_stmt|;
comment|// TODO: sanitize against injection attacks!?
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyValue
argument_list|>
name|bindings
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"uuid"
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|uuid
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Result
name|result
init|=
name|root
operator|.
name|getQueryEngine
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"SELECT * FROM [nt:base] WHERE PROPERTY(["
operator|+
name|pName
operator|+
literal|"], '"
operator|+
name|reference
operator|+
literal|"') = $uuid"
operator|+
name|QueryEngine
operator|.
name|INTERNAL_SQL2_QUERY
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
name|bindings
argument_list|,
name|NO_MAPPINGS
argument_list|)
decl_stmt|;
return|return
name|findPaths
argument_list|(
name|result
argument_list|,
name|uuid
argument_list|,
name|propertyName
argument_list|,
name|nodeTypeNames
argument_list|,
name|weak
condition|?
name|Type
operator|.
name|WEAKREFERENCE
else|:
name|Type
operator|.
name|REFERENCE
argument_list|,
name|weak
condition|?
name|Type
operator|.
name|WEAKREFERENCES
else|:
name|Type
operator|.
name|REFERENCES
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"query failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
block|}
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|findPaths
parameter_list|(
specifier|final
name|Result
name|result
parameter_list|,
specifier|final
name|String
name|uuid
parameter_list|,
specifier|final
name|String
name|propertyName
parameter_list|,
specifier|final
name|String
index|[]
name|nodeTypeNames
parameter_list|,
specifier|final
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|,
specifier|final
name|Type
argument_list|<
name|?
argument_list|>
name|types
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|concat
argument_list|(
name|transform
argument_list|(
name|result
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|RowToPaths
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
class|class
name|RowToPaths
implements|implements
name|Function
argument_list|<
name|ResultRow
argument_list|,
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|apply
parameter_list|(
name|ResultRow
name|row
parameter_list|)
block|{
specifier|final
name|String
name|rowPath
init|=
name|row
operator|.
name|getPath
argument_list|()
decl_stmt|;
class|class
name|PropertyToPath
implements|implements
name|Function
argument_list|<
name|PropertyState
argument_list|,
name|String
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|PropertyState
name|pState
parameter_list|)
block|{
if|if
condition|(
name|pState
operator|.
name|isArray
argument_list|()
condition|)
block|{
if|if
condition|(
name|pState
operator|.
name|getType
argument_list|()
operator|==
name|types
condition|)
block|{
for|for
control|(
name|String
name|value
range|:
name|pState
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
if|if
condition|(
name|uuid
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|rowPath
argument_list|,
name|pState
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|pState
operator|.
name|getType
argument_list|()
operator|==
name|type
condition|)
block|{
if|if
condition|(
name|uuid
operator|.
name|equals
argument_list|(
name|pState
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|rowPath
argument_list|,
name|pState
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
comment|// skip references from the version storage (OAK-1196)
if|if
condition|(
operator|!
name|rowPath
operator|.
name|startsWith
argument_list|(
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
argument_list|)
condition|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|rowPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeTypeNames
operator|.
name|length
operator|==
literal|0
operator|||
name|containsNodeType
argument_list|(
name|tree
argument_list|,
name|nodeTypeNames
argument_list|)
condition|)
block|{
if|if
condition|(
name|propertyName
operator|==
literal|null
condition|)
block|{
return|return
name|filter
argument_list|(
name|transform
argument_list|(
name|tree
operator|.
name|getProperties
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|PropertyToPath
argument_list|()
argument_list|)
argument_list|,
name|notNull
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// for a fixed property name, we don't need to look for it, but just assume that
comment|// the search found the correct one
return|return
name|singletonIterator
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|rowPath
argument_list|,
name|propertyName
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|emptyIterator
argument_list|()
return|;
block|}
specifier|private
name|boolean
name|containsNodeType
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
index|[]
name|nodeTypeNames
parameter_list|)
block|{
for|for
control|(
name|String
name|ntName
range|:
name|nodeTypeNames
control|)
block|{
if|if
condition|(
name|nodeTypeManager
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|ntName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|String
name|resolveUUID
parameter_list|(
name|String
name|uuid
parameter_list|)
block|{
return|return
name|resolveUUID
argument_list|(
name|StringPropertyState
operator|.
name|stringProperty
argument_list|(
literal|""
argument_list|,
name|uuid
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|String
name|resolveUUID
parameter_list|(
name|PropertyState
name|uuid
parameter_list|)
block|{
return|return
name|resolveUUID
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|uuid
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|String
name|resolveUUID
parameter_list|(
name|PropertyValue
name|uuid
parameter_list|)
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|bindings
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"id"
argument_list|,
name|uuid
argument_list|)
decl_stmt|;
name|Result
name|result
init|=
name|root
operator|.
name|getQueryEngine
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"SELECT * FROM [nt:base] WHERE [jcr:uuid] = $id"
operator|+
name|QueryEngine
operator|.
name|INTERNAL_SQL2_QUERY
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
name|bindings
argument_list|,
name|NO_MAPPINGS
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ResultRow
name|rr
range|:
name|result
operator|.
name|getRows
argument_list|()
control|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"multiple results for identifier lookup: "
operator|+
name|path
operator|+
literal|" vs. "
operator|+
name|rr
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
name|path
operator|=
name|rr
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|path
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|ex
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"query failed"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

