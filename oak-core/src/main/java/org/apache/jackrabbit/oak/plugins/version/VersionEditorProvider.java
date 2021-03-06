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
name|version
package|;
end_package

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
name|CompositeEditor
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
name|Editor
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
name|EditorProvider
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
name|SubtreeEditor
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
name|VisibleEditor
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
name|NodeState
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
name|JcrConstants
operator|.
name|JCR_SYSTEM
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_VERSIONSTORAGE
import|;
end_import

begin_comment
comment|/**  * A provider creating two editors: {@link VersionEditor}  * {@link VersionStorageEditor}.  *<p>  * Historically, it has been used to initalize the Jcr repository. Now, the  * more general {@link VersionHook} should be passed there using the {@code with()}  * method.  */
end_comment

begin_class
class|class
name|VersionEditorProvider
implements|implements
name|EditorProvider
block|{
annotation|@
name|Override
specifier|public
name|Editor
name|getRootEditor
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
if|if
condition|(
operator|!
name|builder
operator|.
name|hasChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|NodeBuilder
name|system
init|=
name|builder
operator|.
name|child
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|system
operator|.
name|hasChildNode
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|NodeBuilder
name|versionStorage
init|=
name|system
operator|.
name|child
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
decl_stmt|;
return|return
operator|new
name|VisibleEditor
argument_list|(
operator|new
name|CompositeEditor
argument_list|(
operator|new
name|VersionEditor
argument_list|(
name|versionStorage
argument_list|,
name|builder
argument_list|,
name|info
argument_list|)
argument_list|,
operator|new
name|SubtreeEditor
argument_list|(
operator|new
name|VersionStorageEditor
argument_list|(
name|versionStorage
argument_list|,
name|builder
argument_list|)
argument_list|,
name|JCR_SYSTEM
argument_list|,
name|JCR_VERSIONSTORAGE
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

