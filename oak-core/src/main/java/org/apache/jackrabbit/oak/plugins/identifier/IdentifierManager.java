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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

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
name|Map
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
name|base
operator|.
name|Predicate
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
name|Iterables
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
name|Sets
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
name|PropertyStates
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
operator|.
name|STRING
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
name|Type
operator|.
name|STRINGS
import|;
end_import

begin_comment
comment|/**  * IdentifierManager...  */
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
try|try
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
literal|"UTF-8"
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
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected error while creating uuid"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
comment|/**      *      * @param uuid      * @throws IllegalArgumentException If the specified uuid has an invalid format.      */
specifier|public
specifier|static
name|void
name|checkUUIDFormat
parameter_list|(
name|String
name|uuid
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|UUID
operator|.
name|fromString
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
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
operator|==
literal|null
condition|)
block|{
comment|// TODO calculate the identifier from closest referenceable parent
comment|// TODO and a relative path irrespective of the accessibility of the parent node(s)
return|return
name|tree
operator|.
name|getPath
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
block|}
comment|/**      * The tree identified by the specified {@code identifier} or {@code null}.      *      * @param identifier The identifier of the Node such as exposed by {@link javax.jcr.Node#getIdentifier()}      * @return The tree with the given {@code identifier} or {@code null} if no      * such tree exists or isn't accessible to the content session.      */
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
name|isValidUUID
argument_list|(
name|identifier
argument_list|)
condition|)
block|{
name|String
name|path
init|=
name|resolveUUID
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
return|return
operator|(
name|path
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
else|else
block|{
comment|// TODO as stated in NodeDelegate#getIdentifier() a non-uuid ID should
comment|// TODO consisting of closest referenceable parent and a relative path
comment|// TODO irrespective of the accessibility of the parent node(s)
return|return
name|root
operator|.
name|getTree
argument_list|(
name|identifier
argument_list|)
return|;
block|}
block|}
comment|/**      * The path of the tree identified by the specified {@code identifier} or {@code null}.      *      * @param identifier The identifier of the Tree such as exposed by {@link javax.jcr.Node#getIdentifier()}      * @return The tree with the given {@code identifier} or {@code null} if no      * such tree exists or isn't accessible to the content session.      */
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
if|if
condition|(
name|isValidUUID
argument_list|(
name|identifier
argument_list|)
condition|)
block|{
return|return
name|resolveUUID
argument_list|(
name|identifier
argument_list|)
return|;
block|}
else|else
block|{
comment|// TODO as stated in NodeDelegate#getIdentifier() a non-uuid ID should
comment|// TODO consisting of closest referenceable parent and a relative path
comment|// TODO irrespective of the accessibility of the parent node(s)
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
return|return
name|tree
operator|==
literal|null
condition|?
literal|null
else|:
name|tree
operator|.
name|getPath
argument_list|()
return|;
block|}
block|}
comment|/**      * Returns the path of the tree references by the specified (weak)      * reference {@code CoreValue value}.      *      * @param referenceValue A (weak) reference value.      * @return The tree with the given {@code identifier} or {@code null} if no      * such tree exists or isn't accessible to the content session.      */
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
comment|/**      * Searches all reference properties to the specified {@code tree} that match      * the given name and node type constraints.      *      * @param weak  if {@code true} only weak references are returned. Otherwise only      *              hard references are returned.      * @param tree The tree for which references should be searched.      * @param propertyName A name constraint for the reference properties;      * {@code null} if no constraint should be enforced.      * @param nodeTypeNames Node type constraints to be enforced when using      * for reference properties.      * @return A set of oak paths of those reference properties referring to the      * specified {@code tree} and matching the constraints.      */
annotation|@
name|Nonnull
specifier|public
name|Set
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
name|isReferenceable
argument_list|(
name|tree
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
else|else
block|{
try|try
block|{
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
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
name|bindings
argument_list|,
name|root
argument_list|,
operator|new
name|NamePathMapper
operator|.
name|Default
argument_list|()
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Iterables
operator|.
name|transform
argument_list|(
name|result
operator|.
name|getRows
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ResultRow
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|ResultRow
name|row
parameter_list|)
block|{
name|String
name|pName
init|=
name|propertyName
operator|==
literal|null
condition|?
name|findProperty
argument_list|(
name|row
operator|.
name|getPath
argument_list|()
argument_list|,
name|uuid
argument_list|)
else|:
name|propertyName
decl_stmt|;
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|row
operator|.
name|getPath
argument_list|()
argument_list|,
name|pName
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeTypeNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|paths
operator|=
name|Iterables
operator|.
name|filter
argument_list|(
name|paths
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|tree
operator|!=
literal|null
condition|)
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
name|hasType
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
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|paths
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
block|}
specifier|private
name|String
name|findProperty
parameter_list|(
name|String
name|path
parameter_list|,
specifier|final
name|String
name|uuid
parameter_list|)
block|{
comment|// TODO (OAK-220) PropertyState can only be accessed from parent tree
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
assert|assert
name|tree
operator|!=
literal|null
assert|;
specifier|final
name|PropertyState
name|refProp
init|=
name|Iterables
operator|.
name|find
argument_list|(
name|tree
operator|.
name|getProperties
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
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
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|uuid
operator|.
name|equals
argument_list|(
name|pState
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|refProp
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|boolean
name|hasType
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|ntName
parameter_list|)
block|{
comment|// TODO use NodeType.isNodeType to determine type membership instead of equality on type names
name|PropertyState
name|pType
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|pType
operator|!=
literal|null
condition|)
block|{
name|String
name|primaryType
init|=
name|pType
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|ntName
operator|.
name|equals
argument_list|(
name|primaryType
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
name|PropertyState
name|pMixin
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|pMixin
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|mixinType
range|:
name|pMixin
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
if|if
condition|(
name|ntName
operator|.
name|equals
argument_list|(
name|mixinType
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
name|boolean
name|isReferenceable
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
comment|// TODO add proper implementation include node type eval
return|return
name|tree
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
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
name|PropertyStates
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
name|PropertyValues
operator|.
name|create
argument_list|(
name|uuid
argument_list|)
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
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
name|bindings
argument_list|,
name|root
argument_list|,
operator|new
name|NamePathMapper
operator|.
name|Default
argument_list|()
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

