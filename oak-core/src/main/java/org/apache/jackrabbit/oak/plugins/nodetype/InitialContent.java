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
name|nodetype
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
name|security
operator|.
name|OpenSecurityProvider
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
comment|/**  * {@code InitialContent} implements a {@link MicroKernelTracker} and  * registers built-in node types when the micro kernel becomes available.  */
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
comment|// FIXME: depends on name mangling
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
literal|"\"jcr:versionStorage\" :{\"jcr:primaryType\":\"nam:rep:versionStorage\"},"
operator|+
literal|"\"jcr:nodeTypes\"      :{\"jcr:primaryType\":\"nam:rep:nodeTypes\"},"
operator|+
literal|"\"jcr:activities\"     :{\"jcr:primaryType\":\"nam:rep:Activities\"},"
operator|+
literal|"\"rep:privileges\"     :{\"jcr:primaryType\":\"nam:rep:Privileges\"}}"
operator|+
literal|"+\"rep:security\":{"
operator|+
literal|"\"jcr:primaryType\":\"nam:rep:AuthorizableFolder\","
operator|+
literal|"\"rep:authorizables\":{"
operator|+
literal|"\"jcr:primaryType\":\"nam:rep:AuthorizableFolder\","
operator|+
literal|"\"rep:users\":{"
operator|+
literal|"\"jcr:primaryType\":\"nam:rep:AuthorizableFolder\","
operator|+
literal|"\"a\":{"
operator|+
literal|"\"jcr:primaryType\":\"nam:rep:AuthorizableFolder\","
operator|+
literal|"\"ad\":{"
operator|+
literal|"\"jcr:primaryType\":\"nam:rep:AuthorizableFolder\","
operator|+
literal|"\"admin\":{"
operator|+
literal|"\"jcr:primaryType\":\"nam:rep:User\","
operator|+
literal|"\"jcr:uuid\":\"21232f29-7a57-35a7-8389-4a0e4a801fc3\","
operator|+
literal|"\"rep:principalName\":\"admin\","
operator|+
literal|"\"rep:authorizableId\":\"admin\","
operator|+
literal|"\"rep:password\":\"{SHA-256}9e515755e95513ce-1000-0696716f8baf8890a35eda1b9f2d5a4e727d1c7e1c062f03180dcc2a20f61f3b\"}},"
operator|+
literal|"\"an\":{ "
operator|+
literal|"\"jcr:primaryType\":\"nam:rep:AuthorizableFolder\","
operator|+
literal|"\"anonymous\":{"
operator|+
literal|"\"jcr:primaryType\":\"nam:rep:User\","
operator|+
literal|"\"jcr:uuid\":\"294de355-7d9d-30b3-92d8-a1e6aab028cf\","
operator|+
literal|"\"rep:principalName\":\"anonymous\","
operator|+
literal|"\"rep:authorizableId\":\"anonymous\"}}"
operator|+
literal|"}}}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|root
operator|.
name|hasChildNode
argument_list|(
literal|"oak:index"
argument_list|)
condition|)
block|{
comment|// FIXME: user-mgt related unique properties (rep:authorizableId, rep:principalName) are implementation detail and not generic for repo
comment|// FIXME: rep:principalName only needs to be unique if defined with user/group nodes -> add defining nt-info to uniqueness constraint otherwise ac-editing will fail.
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"oak:index\":{\"jcr:uuid\":{\"jcr:primaryType\":\"nam:oak:queryIndexDefinition\",\"unique\":true},\"rep:authorizableId\":{\"jcr:primaryType\":\"nam:oak:queryIndexDefinition\",\"unique\":true},\"rep:principalName\":{\"jcr:primaryType\":\"nam:oak:queryIndexDefinition\",\"unique\":true}}"
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
name|createRoot
argument_list|(
name|mk
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Root
name|createRoot
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|oak
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: this shouldn't be needed
try|try
block|{
return|return
name|oak
operator|.
name|createContentRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getLatestRoot
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to create a Root"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

