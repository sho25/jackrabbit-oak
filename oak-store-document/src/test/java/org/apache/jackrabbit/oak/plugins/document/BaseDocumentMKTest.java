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

begin_comment
comment|/**  *<code>BaseDocumentMKTest</code>...  */
end_comment

begin_class
specifier|public
class|class
name|BaseDocumentMKTest
extends|extends
name|DocumentMKTestBase
block|{
specifier|protected
name|DocumentMK
name|mk
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|initDocumentMK
parameter_list|()
block|{
name|mk
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|open
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
annotation|@
name|After
specifier|public
name|void
name|disposeDocumentMK
parameter_list|()
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
name|mk
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
