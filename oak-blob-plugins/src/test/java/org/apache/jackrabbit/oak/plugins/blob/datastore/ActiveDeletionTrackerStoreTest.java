begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|blob
operator|.
name|datastore
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|Comparator
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
name|Set
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
name|Strings
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
name|Lists
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
name|oak
operator|.
name|commons
operator|.
name|FileIOUtils
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
name|blob
operator|.
name|SharedDataStore
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
name|blob
operator|.
name|datastore
operator|.
name|BlobIdTracker
operator|.
name|ActiveDeletionTracker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|valueOf
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|UUID
operator|.
name|randomUUID
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
name|commons
operator|.
name|FileIOUtils
operator|.
name|readStringsAsSet
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
name|plugins
operator|.
name|blob
operator|.
name|datastore
operator|.
name|DataStoreUtils
operator|.
name|getBlobStore
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeNoException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeThat
import|;
end_import

begin_comment
comment|/**  * Test for BlobIdTracker.ActiveDeletionTracker to test tracking removed blob ids.  */
end_comment

begin_class
specifier|public
class|class
name|ActiveDeletionTrackerStoreTest
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
name|ActiveDeletionTrackerStoreTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|File
name|root
decl_stmt|;
name|SharedDataStore
name|dataStore
decl_stmt|;
name|ActiveDeletionTracker
name|tracker
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|String
name|repoId
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|assumeThat
argument_list|(
name|getBlobStore
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|SharedDataStore
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assumeNoException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|root
operator|=
name|folder
operator|.
name|newFolder
argument_list|()
expr_stmt|;
if|if
condition|(
name|dataStore
operator|==
literal|null
condition|)
block|{
name|dataStore
operator|=
name|getBlobStore
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|repoId
operator|=
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|tracker
operator|=
name|initTracker
argument_list|()
expr_stmt|;
block|}
specifier|private
name|ActiveDeletionTracker
name|initTracker
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ActiveDeletionTracker
argument_list|(
name|root
argument_list|,
name|repoId
argument_list|)
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|folder
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|track
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|initAdd
init|=
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|retrieve
argument_list|(
name|tracker
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect elements after add snapshot"
argument_list|,
name|initAdd
argument_list|,
name|retrieved
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|filter
parameter_list|()
throws|throws
name|Exception
block|{
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
argument_list|,
name|folder
argument_list|)
expr_stmt|;
name|File
name|toFilter
init|=
name|create
argument_list|(
name|range
argument_list|(
literal|7
argument_list|,
literal|10
argument_list|)
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|filtered
init|=
name|tracker
operator|.
name|filter
argument_list|(
name|toFilter
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"More elements after filtering"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|filtered
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
argument_list|,
name|folder
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|toFilter
init|=
name|combine
argument_list|(
name|range
argument_list|(
literal|7
argument_list|,
literal|10
argument_list|)
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|toFilterFile
init|=
name|create
argument_list|(
name|toFilter
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|filtered
init|=
name|tracker
operator|.
name|filter
argument_list|(
name|toFilterFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect elements after filtering"
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|filtered
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|filterWithExtraElements
parameter_list|()
throws|throws
name|Exception
block|{
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
argument_list|,
name|folder
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|toFilter
init|=
name|combine
argument_list|(
name|combine
argument_list|(
name|range
argument_list|(
literal|7
argument_list|,
literal|10
argument_list|)
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|,
name|range
argument_list|(
literal|21
argument_list|,
literal|25
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|toFilterFile
init|=
name|create
argument_list|(
name|toFilter
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|filtered
init|=
name|tracker
operator|.
name|filter
argument_list|(
name|toFilterFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect elements after filtering"
argument_list|,
name|combine
argument_list|(
name|range
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|range
argument_list|(
literal|21
argument_list|,
literal|25
argument_list|)
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|filtered
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|reconcileNone
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|initAdd
init|=
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|List
name|toReconcile
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|File
name|toFilter
init|=
name|create
argument_list|(
name|toReconcile
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|reconcile
argument_list|(
name|toFilter
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|retrieve
argument_list|(
name|tracker
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect elements with after reconciliation"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|toReconcile
argument_list|)
argument_list|,
name|retrieved
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|reconcile
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|initAdd
init|=
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|toReconcile
init|=
name|combine
argument_list|(
name|range
argument_list|(
literal|7
argument_list|,
literal|10
argument_list|)
argument_list|,
name|range
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|toFilter
init|=
name|create
argument_list|(
name|toReconcile
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|reconcile
argument_list|(
name|toFilter
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|retrieve
argument_list|(
name|tracker
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect elements with after reconciliation"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|toReconcile
argument_list|)
argument_list|,
name|retrieved
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addCloseRestart
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|initAdd
init|=
name|add
argument_list|(
name|tracker
argument_list|,
name|range
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|this
operator|.
name|tracker
operator|=
name|initTracker
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|retrieve
argument_list|(
name|tracker
argument_list|,
name|folder
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect elements after safe restart"
argument_list|,
name|initAdd
argument_list|,
name|retrieved
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|add
parameter_list|(
name|ActiveDeletionTracker
name|store
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|ints
parameter_list|,
name|TemporaryFolder
name|folder
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|folder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|FileIOUtils
operator|.
name|writeStrings
argument_list|(
name|ints
operator|.
name|iterator
argument_list|()
argument_list|,
name|f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|store
operator|.
name|track
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|ints
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|File
name|create
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|ints
parameter_list|,
name|TemporaryFolder
name|folder
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|folder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|FileIOUtils
operator|.
name|writeStrings
argument_list|(
name|ints
operator|.
name|iterator
argument_list|()
argument_list|,
name|f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|retrieve
parameter_list|(
name|ActiveDeletionTracker
name|store
parameter_list|,
name|TemporaryFolder
name|folder
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|folder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|readStringsAsSet
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|store
operator|.
name|retrieve
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|retrieved
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|range
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|min
init|;
name|i
operator|<=
name|max
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|Strings
operator|.
name|padStart
argument_list|(
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|2
argument_list|,
literal|'0'
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|combine
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|first
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|second
parameter_list|)
block|{
name|first
operator|.
name|addAll
argument_list|(
name|second
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|first
argument_list|,
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|s1
argument_list|)
operator|.
name|compareTo
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|s2
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|first
return|;
block|}
block|}
end_class

end_unit

