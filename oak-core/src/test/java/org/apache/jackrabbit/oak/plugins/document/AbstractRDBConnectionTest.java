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
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
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
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBOptions
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

begin_comment
comment|/**  * Base class for test cases that need a {@link DataSource}  * to a clean test database. Tests in subclasses are automatically  * skipped if the configured database connection can not be created.  */
end_comment

begin_class
specifier|public
class|class
name|AbstractRDBConnectionTest
extends|extends
name|DocumentMKTestBase
block|{
specifier|protected
name|DataSource
name|dataSource
decl_stmt|;
specifier|protected
name|DocumentMK
name|mk
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|fname
init|=
operator|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
operator|)
operator|.
name|isDirectory
argument_list|()
condition|?
literal|"target/"
else|:
literal|""
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|RAWURL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-url"
argument_list|,
literal|"jdbc:h2:file:./target/h2test"
argument_list|)
decl_stmt|;
specifier|protected
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
literal|""
argument_list|)
decl_stmt|;
specifier|protected
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
specifier|protected
specifier|static
specifier|final
name|String
name|URL
init|=
name|RAWURL
operator|.
name|replace
argument_list|(
literal|"{fname}"
argument_list|,
name|fname
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|checkRDBAvailable
parameter_list|()
block|{     }
annotation|@
name|Before
specifier|public
name|void
name|setUpConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|dataSource
operator|=
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
expr_stmt|;
name|Revision
operator|.
name|setClock
argument_list|(
name|getTestClock
argument_list|()
argument_list|)
expr_stmt|;
name|mk
operator|=
name|newBuilder
argument_list|(
name|dataSource
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|DocumentMK
operator|.
name|Builder
name|newBuilder
parameter_list|(
name|DataSource
name|db
parameter_list|)
throws|throws
name|Exception
block|{
name|RDBOptions
name|opt
init|=
operator|new
name|RDBOptions
argument_list|()
operator|.
name|tablePrefix
argument_list|(
literal|"T"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|"-"
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|dropTablesOnClose
argument_list|(
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|clock
argument_list|(
name|getTestClock
argument_list|()
argument_list|)
operator|.
name|setRDBConnection
argument_list|(
name|dataSource
argument_list|,
name|opt
argument_list|)
return|;
block|}
specifier|protected
name|Clock
name|getTestClock
parameter_list|()
throws|throws
name|InterruptedException
block|{
return|return
name|Clock
operator|.
name|SIMPLE
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDownConnection
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|mk
operator|!=
literal|null
condition|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|DocumentMK
name|getDocumentMK
parameter_list|()
block|{
return|return
name|mk
return|;
block|}
specifier|protected
specifier|static
name|byte
index|[]
name|readFully
parameter_list|(
name|DocumentMK
name|mk
parameter_list|,
name|String
name|blobId
parameter_list|)
block|{
name|int
name|remaining
init|=
operator|(
name|int
operator|)
name|mk
operator|.
name|getLength
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|remaining
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|int
name|count
init|=
name|mk
operator|.
name|read
argument_list|(
name|blobId
argument_list|,
name|offset
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|,
name|remaining
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|offset
operator|+=
name|count
expr_stmt|;
name|remaining
operator|-=
name|count
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
block|}
end_class

end_unit

