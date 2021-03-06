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
name|principalbased
operator|.
name|impl
package|;
end_package

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
name|Strings
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
name|plugins
operator|.
name|tree
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionPattern
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
name|restriction
operator|.
name|RestrictionProvider
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
name|privilege
operator|.
name|PrivilegeBits
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
name|privilege
operator|.
name|PrivilegeBitsProvider
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|Iterator
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

begin_class
class|class
name|EntryCache
implements|implements
name|Constants
block|{
specifier|private
specifier|final
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
comment|/**      * Mapping effective path (empty string representing the null path) to the permission entries defined for each      * effective path. Note that this map does not record the name or nature (group vs non-group) of the principal for      * which the entries have been defined. Similarly it ignores the order of entries as the implementation only      * supports 'allow' entries.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PermissionEntry
argument_list|>
argument_list|>
name|entries
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|EntryCache
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|Iterable
argument_list|<
name|String
argument_list|>
name|principalPathSet
parameter_list|,
annotation|@
name|NotNull
name|RestrictionProvider
name|restrictionProvider
parameter_list|)
block|{
name|this
operator|.
name|restrictionProvider
operator|=
name|restrictionProvider
expr_stmt|;
name|this
operator|.
name|bitsProvider
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|principalPath
range|:
name|principalPathSet
control|)
block|{
name|Tree
name|policyTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|principalPath
argument_list|,
name|Constants
operator|.
name|REP_PRINCIPAL_POLICY
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|policyTree
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|Tree
name|child
range|:
name|policyTree
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|Constants
operator|.
name|NT_REP_PRINCIPAL_ENTRY
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|child
argument_list|)
argument_list|)
condition|)
block|{
name|PermissionEntryImpl
name|entry
init|=
operator|new
name|PermissionEntryImpl
argument_list|(
name|child
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|entry
operator|.
name|effectivePath
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PermissionEntry
argument_list|>
name|list
init|=
name|entries
operator|.
name|computeIfAbsent
argument_list|(
name|key
argument_list|,
name|k
lambda|->
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|NotNull
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|getEntries
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
name|Iterable
argument_list|<
name|PermissionEntry
argument_list|>
name|list
init|=
name|entries
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
operator|(
name|list
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
name|emptyIterator
argument_list|()
else|:
name|list
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|private
specifier|final
class|class
name|PermissionEntryImpl
implements|implements
name|PermissionEntry
block|{
specifier|private
specifier|final
name|String
name|effectivePath
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeBits
name|privilegeBits
decl_stmt|;
specifier|private
name|RestrictionPattern
name|pattern
decl_stmt|;
specifier|private
name|PermissionEntryImpl
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|entryTree
parameter_list|)
block|{
name|effectivePath
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|TreeUtil
operator|.
name|getString
argument_list|(
name|entryTree
argument_list|,
name|REP_EFFECTIVE_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|privilegeBits
operator|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|entryTree
operator|.
name|getProperty
argument_list|(
name|REP_PRIVILEGES
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|pattern
operator|=
name|restrictionProvider
operator|.
name|getPattern
argument_list|(
name|effectivePath
argument_list|,
name|restrictionProvider
operator|.
name|readRestrictions
argument_list|(
name|effectivePath
argument_list|,
name|entryTree
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|public
name|PrivilegeBits
name|getPrivilegeBits
parameter_list|()
block|{
return|return
name|privilegeBits
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|appliesTo
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
return|return
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|effectivePath
argument_list|,
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|pattern
operator|.
name|matches
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
annotation|@
name|NotNull
name|String
name|treePath
parameter_list|)
block|{
return|return
name|pattern
operator|.
name|matches
argument_list|(
name|treePath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
block|{
return|return
name|pattern
operator|.
name|matches
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

