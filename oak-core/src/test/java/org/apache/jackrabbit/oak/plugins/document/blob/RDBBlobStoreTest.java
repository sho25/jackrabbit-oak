begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|blob
package|;
end_package

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
name|RDBBlobStore
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
name|RDBDataSourceFactory
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
name|blob
operator|.
name|AbstractBlobStoreTest
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

begin_comment
comment|/**  * Tests the RDBBlobStore implementation.  */
end_comment

begin_class
specifier|public
class|class
name|RDBBlobStoreTest
extends|extends
name|AbstractBlobStoreTest
block|{
specifier|private
name|RDBBlobStore
name|blobStore
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|URL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-url"
argument_list|,
literal|"jdbc:h2:mem:oakblobs"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USERNAME
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-user"
argument_list|,
literal|"sa"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PASSWD
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-passwd"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|blobStore
operator|=
operator|new
name|RDBBlobStore
argument_list|(
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|URL
argument_list|,
name|USERNAME
argument_list|,
name|PASSWD
argument_list|)
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|setBlockSize
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|setBlockSizeMin
argument_list|(
literal|48
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|blobStore
expr_stmt|;
name|empty
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|empty
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|empty
parameter_list|(
name|RDBBlobStore
name|blobStore
parameter_list|)
throws|throws
name|Exception
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|blobStore
operator|.
name|getAllChunkIds
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|blobStore
operator|.
name|deleteChunks
argument_list|(
name|ids
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

