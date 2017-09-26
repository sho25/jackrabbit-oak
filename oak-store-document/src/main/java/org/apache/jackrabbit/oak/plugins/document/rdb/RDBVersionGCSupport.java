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
name|document
operator|.
name|rdb
package|;
end_package

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
name|Iterables
operator|.
name|filter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Arrays
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
name|List
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
name|plugins
operator|.
name|document
operator|.
name|Collection
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
name|document
operator|.
name|DocumentStoreException
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
name|document
operator|.
name|NodeDocument
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
name|document
operator|.
name|NodeDocument
operator|.
name|SplitDocType
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
name|document
operator|.
name|RevisionVector
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
name|document
operator|.
name|VersionGCSupport
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
name|document
operator|.
name|rdb
operator|.
name|RDBDocumentStore
operator|.
name|QueryCondition
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
name|document
operator|.
name|util
operator|.
name|CloseableIterable
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
name|document
operator|.
name|util
operator|.
name|Utils
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
name|stats
operator|.
name|Clock
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

begin_comment
comment|/**  * RDB specific version of {@link VersionGCSupport} which uses an extended query  * interface to fetch required {@link NodeDocument}s.  */
end_comment

begin_class
specifier|public
class|class
name|RDBVersionGCSupport
extends|extends
name|VersionGCSupport
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RDBVersionGCSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|RDBDocumentStore
name|store
decl_stmt|;
specifier|public
name|RDBVersionGCSupport
parameter_list|(
name|RDBDocumentStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|getPossiblyDeletedDocs
parameter_list|(
specifier|final
name|long
name|fromModified
parameter_list|,
specifier|final
name|long
name|toModified
parameter_list|)
block|{
name|List
argument_list|<
name|QueryCondition
argument_list|>
name|conditions
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryCondition
argument_list|>
argument_list|()
decl_stmt|;
name|conditions
operator|.
name|add
argument_list|(
operator|new
name|QueryCondition
argument_list|(
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|,
literal|"="
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|conditions
operator|.
name|add
argument_list|(
operator|new
name|QueryCondition
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
literal|"<"
argument_list|,
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
name|toModified
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|conditions
operator|.
name|add
argument_list|(
operator|new
name|QueryCondition
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
literal|">="
argument_list|,
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
name|fromModified
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|store
operator|.
name|queryAsIterable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|RDBDocumentStore
operator|.
name|EMPTY_KEY_PATTERN
argument_list|,
name|conditions
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|identifyGarbage
parameter_list|(
specifier|final
name|Set
argument_list|<
name|SplitDocType
argument_list|>
name|gcTypes
parameter_list|,
specifier|final
name|RevisionVector
name|sweepRevs
parameter_list|,
specifier|final
name|long
name|oldestRevTimeStamp
parameter_list|)
block|{
name|List
argument_list|<
name|QueryCondition
argument_list|>
name|conditions
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
comment|// absent support for SDTYPE as indexed property: exclude those
comment|// documents from the query which definitively aren't split documents
name|List
argument_list|<
name|String
argument_list|>
name|excludeKeyPatterns
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"_:/%"
argument_list|,
literal|"__:/%"
argument_list|,
literal|"___:/%"
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|it
init|=
name|store
operator|.
name|queryAsIterable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|excludeKeyPatterns
argument_list|,
name|conditions
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|CloseableIterable
operator|.
name|wrap
argument_list|(
name|filter
argument_list|(
name|it
argument_list|,
operator|new
name|Predicate
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|NodeDocument
name|doc
parameter_list|)
block|{
return|return
name|gcTypes
operator|.
name|contains
argument_list|(
name|doc
operator|.
name|getSplitDocType
argument_list|()
argument_list|)
operator|&&
name|doc
operator|.
name|hasAllRevisionLessThan
argument_list|(
name|oldestRevTimeStamp
argument_list|)
operator|&&
operator|!
name|isDefaultNoBranchSplitNewerThan
argument_list|(
name|doc
argument_list|,
name|sweepRevs
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
operator|(
name|Closeable
operator|)
name|it
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getOldestDeletedOnceTimestamp
parameter_list|(
name|Clock
name|clock
parameter_list|,
name|long
name|precisionMs
parameter_list|)
block|{
name|long
name|modifiedMs
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"getOldestDeletedOnceTimestamp()<- start"
argument_list|)
expr_stmt|;
try|try
block|{
name|modifiedMs
operator|=
name|store
operator|.
name|getMinValue
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|RDBDocumentStore
operator|.
name|EMPTY_KEY_PATTERN
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|QueryCondition
argument_list|(
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|,
literal|"="
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getMinValue(MODIFIED)"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|modifiedMs
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getOldestDeletedOnceTimestamp() -> {}"
argument_list|,
name|Utils
operator|.
name|timestampToString
argument_list|(
name|modifiedMs
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|modifiedMs
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getOldestDeletedOnceTimestamp() -> none found, return current time"
argument_list|)
expr_stmt|;
return|return
name|clock
operator|.
name|getTime
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDeletedOnceCount
parameter_list|()
block|{
return|return
name|store
operator|.
name|queryCount
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|RDBDocumentStore
operator|.
name|EMPTY_KEY_PATTERN
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|QueryCondition
argument_list|(
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|,
literal|"="
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit
