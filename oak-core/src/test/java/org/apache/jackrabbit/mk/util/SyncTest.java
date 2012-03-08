begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|util
package|;
end_package

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
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|MultiMkTestBase
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
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_comment
comment|/**  * Test the sync util.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|SyncTest
extends|extends
name|MultiMkTestBase
block|{
specifier|public
name|SyncTest
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIterator
parameter_list|()
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"n"
operator|+
name|i
operator|+
literal|"\": {}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
literal|"n"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|Sync
operator|.
name|getAllChildNodeNames
argument_list|(
name|mk
argument_list|,
literal|"/"
argument_list|,
name|head
argument_list|,
literal|2
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|n
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|names
operator|.
name|remove
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|names
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|doTest
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSmallChildNodeBatchSize
parameter_list|()
block|{
name|doTest
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTest
parameter_list|(
name|int
name|childNodeBatchSize
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isSimpleKernel
argument_list|(
name|mk
argument_list|)
condition|)
block|{
comment|// TODO fix test since it incorrectly expects a specific order of child nodes
return|return;
block|}
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"source\": { \"id\": 1, \"plus\": 0, \"a\": { \"x\": 10, \"y\": 20 }, \"b\": {\"z\": 100}, \"d\":{} }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Sync
name|sync
init|=
operator|new
name|Sync
argument_list|()
decl_stmt|;
if|if
condition|(
name|childNodeBatchSize
operator|>
literal|0
condition|)
block|{
name|sync
operator|.
name|setChildNodesPerBatch
argument_list|(
name|childNodeBatchSize
argument_list|)
expr_stmt|;
block|}
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|sync
operator|.
name|setSource
argument_list|(
name|mk
argument_list|,
name|head
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|String
name|diff
init|=
name|syncToString
argument_list|(
name|sync
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"add /source\n"
operator|+
literal|"setProperty /source id=1\n"
operator|+
literal|"setProperty /source plus=0\n"
operator|+
literal|"add /source/a\n"
operator|+
literal|"setProperty /source/a x=10\n"
operator|+
literal|"setProperty /source/a y=20\n"
operator|+
literal|"add /source/b\n"
operator|+
literal|"setProperty /source/b z=100\n"
operator|+
literal|"add /source/d\n"
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"target\": { \"id\": 2, \"minus\": 0, \"a\": { \"x\": 10 }, \"c\": {} }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
name|sync
operator|.
name|setSource
argument_list|(
name|mk
argument_list|,
name|head
argument_list|,
literal|"/source"
argument_list|)
expr_stmt|;
name|sync
operator|.
name|setTarget
argument_list|(
name|mk
argument_list|,
name|head
argument_list|,
literal|"/target"
argument_list|)
expr_stmt|;
name|diff
operator|=
name|syncToString
argument_list|(
name|sync
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"setProperty /target id=1\n"
operator|+
literal|"setProperty /target plus=0\n"
operator|+
literal|"setProperty /target minus=null\n"
operator|+
literal|"setProperty /target/a y=20\n"
operator|+
literal|"add /target/b\n"
operator|+
literal|"setProperty /target/b z=100\n"
operator|+
literal|"add /target/d\n"
operator|+
literal|"remove /target/c\n"
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|sync
operator|.
name|setSource
argument_list|(
name|mk
argument_list|,
name|head
argument_list|,
literal|"/notExist"
argument_list|)
expr_stmt|;
name|sync
operator|.
name|setTarget
argument_list|(
name|mk
argument_list|,
name|head
argument_list|,
literal|"/target"
argument_list|)
expr_stmt|;
name|diff
operator|=
name|syncToString
argument_list|(
name|sync
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"remove /target\n"
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|sync
operator|.
name|setSource
argument_list|(
name|mk
argument_list|,
name|head
argument_list|,
literal|"/notExist"
argument_list|)
expr_stmt|;
name|sync
operator|.
name|setTarget
argument_list|(
name|mk
argument_list|,
name|head
argument_list|,
literal|"/notExist2"
argument_list|)
expr_stmt|;
name|diff
operator|=
name|syncToString
argument_list|(
name|sync
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|syncToString
parameter_list|(
name|Sync
name|sync
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sync
operator|.
name|run
argument_list|(
operator|new
name|Sync
operator|.
name|Handler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|addNode
parameter_list|(
name|String
name|targetPath
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"add "
argument_list|)
operator|.
name|append
argument_list|(
name|targetPath
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeNode
parameter_list|(
name|String
name|targetPath
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"remove "
argument_list|)
operator|.
name|append
argument_list|(
name|targetPath
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|targetPath
parameter_list|,
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"setProperty "
argument_list|)
operator|.
name|append
argument_list|(
name|targetPath
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|property
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

