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
name|segment
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|RandomStringUtils
operator|.
name|randomAlphabetic
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
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
name|assumeTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|Blob
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|gc
operator|.
name|GCMonitor
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeStore
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

begin_comment
comment|/**  *<p>Tests verifying if the repository gets corrupted or not: {@code OAK-2662 SegmentOverflowException in HeavyWriteIT on Jenkins}</p>  *  *<p><b>This test will run for one hour unless it fails</b>, thus it is disabled by default. On the  * command line specify {@code -DSegmentOverflowExceptionIT=true} to enable it. To specify a different  * time out {@code t} value use {@code -Dtimeout=t}  *</p>  *  *<p>If you only want to run this test:<br>  * {@code mvn verify -Dsurefire.skip.ut=true -PintegrationTesting -Dit.test=SegmentOverflowExceptionIT -DSegmentOverflowExceptionIT=true}  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|SegmentOverflowExceptionIT
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
name|SegmentOverflowExceptionIT
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|SegmentOverflowExceptionIT
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|TIMEOUT
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"timeout"
argument_list|,
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|()
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
name|File
name|getFileStoreFolder
parameter_list|()
block|{
return|return
name|folder
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
name|ENABLED
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|volatile
name|boolean
name|compact
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
name|GCMonitor
name|gcMonitor
init|=
operator|new
name|GCMonitor
operator|.
name|Empty
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|skipped
parameter_list|(
name|String
name|reason
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
name|compact
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|cleaned
parameter_list|(
name|long
name|reclaimedSize
parameter_list|,
name|long
name|currentSize
parameter_list|)
block|{
name|compact
operator|=
literal|true
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|FileStore
name|fileStore
init|=
name|fileStoreBuilder
argument_list|(
name|getFileStoreFolder
argument_list|()
argument_list|)
operator|.
name|withGCMonitor
argument_list|(
name|gcMonitor
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|SegmentNodeStore
name|nodeStore
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|snfeCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|<
name|TIMEOUT
condition|)
block|{
try|try
block|{
name|NodeBuilder
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
while|while
condition|(
name|rnd
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|modify
argument_list|(
name|nodeStore
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
name|nodeStore
operator|.
name|merge
argument_list|(
name|root
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
if|if
condition|(
name|compact
condition|)
block|{
name|compact
operator|=
literal|false
expr_stmt|;
name|fileStore
operator|.
name|fullGC
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SegmentNotFoundException
name|snfe
parameter_list|)
block|{
comment|// Usually this can be ignored as SNFEs are somewhat expected here
comment|// due the small retention value for segments.
if|if
condition|(
name|snfeCount
operator|++
operator|>
literal|100
condition|)
block|{
throw|throw
name|snfe
throw|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|modify
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|NodeBuilder
name|nodeBuilder
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|k
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|<
literal|10
condition|)
block|{
if|if
condition|(
operator|!
name|nodeBuilder
operator|.
name|remove
argument_list|()
condition|)
block|{
name|descent
argument_list|(
name|nodeStore
argument_list|,
name|nodeBuilder
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|k
operator|<
literal|40
condition|)
block|{
name|nodeBuilder
operator|.
name|setChildNode
argument_list|(
literal|"N"
operator|+
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|k
operator|<
literal|80
condition|)
block|{
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
literal|"P"
operator|+
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|randomAlphabetic
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|k
operator|<
literal|90
condition|)
block|{
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
literal|"B"
operator|+
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
literal|10000000
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|descent
argument_list|(
name|nodeStore
argument_list|,
name|nodeBuilder
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|descent
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|NodeBuilder
name|nodeBuilder
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|count
init|=
name|nodeBuilder
operator|.
name|getChildNodeCount
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|int
name|c
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
operator|(
name|int
operator|)
name|count
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|Iterables
operator|.
name|get
argument_list|(
name|nodeBuilder
operator|.
name|getChildNodeNames
argument_list|()
argument_list|,
name|c
argument_list|)
decl_stmt|;
name|modify
argument_list|(
name|nodeStore
argument_list|,
name|nodeBuilder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Blob
name|createBlob
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|nodeStore
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

