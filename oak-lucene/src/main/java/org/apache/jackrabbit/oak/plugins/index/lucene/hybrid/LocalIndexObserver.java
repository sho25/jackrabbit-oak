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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|hybrid
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
name|CommitContext
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
name|Observer
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
name|stats
operator|.
name|StatisticsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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

begin_class
specifier|public
class|class
name|LocalIndexObserver
implements|implements
name|Observer
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DocumentQueue
name|docQueue
decl_stmt|;
specifier|public
name|LocalIndexObserver
parameter_list|(
name|DocumentQueue
name|docQueue
parameter_list|,
name|StatisticsProvider
name|sp
parameter_list|)
block|{
name|this
operator|.
name|docQueue
operator|=
name|docQueue
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|root
parameter_list|,
annotation|@
name|NotNull
name|CommitInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|.
name|isExternal
argument_list|()
condition|)
block|{
return|return;
block|}
name|CommitContext
name|commitContext
init|=
operator|(
name|CommitContext
operator|)
name|info
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|)
decl_stmt|;
comment|//Commit done internally i.e. one not using Root/Tree API
if|if
condition|(
name|commitContext
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|LuceneDocumentHolder
name|holder
init|=
operator|(
name|LuceneDocumentHolder
operator|)
name|commitContext
operator|.
name|get
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|)
decl_stmt|;
comment|//Nothing to be indexed
if|if
condition|(
name|holder
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|commitContext
operator|.
name|remove
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|int
name|droppedCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LuceneDoc
name|doc
range|:
name|holder
operator|.
name|getNRTIndexedDocs
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|docQueue
operator|.
name|add
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|droppedCount
operator|++
expr_stmt|;
block|}
block|}
comment|//After nrt docs add all sync indexed docs
comment|//Doing it *after* ensures thar nrt index might catch
comment|//up by the time sync one are finished
name|docQueue
operator|.
name|addAllSynchronously
argument_list|(
name|holder
operator|.
name|getSyncIndexedDocs
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|droppedCount
operator|>
literal|0
condition|)
block|{
comment|//TODO Ensure that log do not flood
name|log
operator|.
name|warn
argument_list|(
literal|"Dropped [{}] docs from indexing as queue is full"
argument_list|,
name|droppedCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

