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
name|mk
operator|.
name|blobs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|jdbcx
operator|.
name|JdbcConnectionPool
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_comment
comment|/**  * Tests the DbBlobStore implementation.  */
end_comment

begin_class
specifier|public
class|class
name|DbBlobStoreTest
extends|extends
name|AbstractBlobStoreTest
block|{
specifier|private
name|Connection
name|sentinel
decl_stmt|;
specifier|private
name|JdbcConnectionPool
name|cp
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"org.h2.Driver"
argument_list|)
expr_stmt|;
name|cp
operator|=
name|JdbcConnectionPool
operator|.
name|create
argument_list|(
literal|"jdbc:h2:mem:"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|sentinel
operator|=
name|cp
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|DbBlobStore
name|blobStore
init|=
operator|new
name|DbBlobStore
argument_list|()
decl_stmt|;
name|blobStore
operator|.
name|setConnectionPool
argument_list|(
name|cp
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
block|}
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
name|sentinel
operator|!=
literal|null
condition|)
block|{
name|sentinel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|cp
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

