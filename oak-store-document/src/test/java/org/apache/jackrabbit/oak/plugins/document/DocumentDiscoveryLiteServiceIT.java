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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests for the DocumentDiscoveryLiteService  */
end_comment

begin_class
specifier|public
class|class
name|DocumentDiscoveryLiteServiceIT
extends|extends
name|BaseDocumentDiscoveryLiteServiceTest
block|{
comment|/**      * This test creates a large number of documentnodestores which it starts,      * runs, stops in a random fashion, always testing to make sure the      * clusterView is correct      */
annotation|@
name|Test
specifier|public
name|void
name|testLargeStartStopFiesta
parameter_list|()
throws|throws
name|Throwable
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"testLargeStartStopFiesta: start, seed="
operator|+
name|SEED
argument_list|)
expr_stmt|;
specifier|final
name|int
name|LOOP_CNT
init|=
literal|50
decl_stmt|;
comment|// with too many loops have also seen mongo
comment|// connections becoming starved thus test
comment|// failed
name|doStartStopFiesta
argument_list|(
name|LOOP_CNT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

