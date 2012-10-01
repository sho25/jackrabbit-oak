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
name|type
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|api
operator|.
name|MicroKernel
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
name|Oak
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
name|lifecycle
operator|.
name|DefaultMicroKernelTracker
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
name|lifecycle
operator|.
name|MicroKernelTracker
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
name|NodeState
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

begin_comment
comment|/**  *<code>InitialContent</code> implements a {@link MicroKernelTracker} and  * registers built-in node types when the micro kernel becomes available.  */
end_comment

begin_class
annotation|@
name|Component
annotation|@
name|Service
argument_list|(
name|MicroKernelTracker
operator|.
name|class
argument_list|)
specifier|public
class|class
name|InitialContent
extends|extends
name|DefaultMicroKernelTracker
block|{
annotation|@
name|Override
specifier|public
name|void
name|available
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|NodeStore
name|nodeStore
init|=
operator|new
name|Oak
argument_list|(
name|mk
argument_list|)
operator|.
name|createNodeStore
argument_list|()
decl_stmt|;
comment|// FIXME: depends on CoreValue's name mangling
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|root
operator|.
name|hasChildNode
argument_list|(
literal|"jcr:system"
argument_list|)
condition|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"jcr:primaryType\":\"nam:rep:root\" "
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"jcr:primaryType\":\"nam:rep:root\""
operator|+
literal|"+\"jcr:system\":{"
operator|+
literal|"\"jcr:primaryType\"    :\"nam:rep:system\","
operator|+
literal|"\":unique\"            :{\"jcr:uuid\":{},\"rep:authorizableId\":{},\"rep:principalName\":{}},"
operator|+
literal|"\"jcr:versionStorage\" :{\"jcr:primaryType\":\"nam:rep:versionStorage\"},"
operator|+
literal|"\"jcr:nodeTypes\"      :{\"jcr:primaryType\":\"nam:rep:nodeTypes\"},"
operator|+
literal|"\"jcr:activities\"     :{\"jcr:primaryType\":\"nam:rep:Activities\"},"
operator|+
literal|"\"rep:privileges\"     :{\"jcr:primaryType\":\"nam:rep:Privileges\"}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|BuiltInNodeTypes
operator|.
name|register
argument_list|(
operator|new
name|Oak
argument_list|(
name|mk
argument_list|)
operator|.
name|createRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

