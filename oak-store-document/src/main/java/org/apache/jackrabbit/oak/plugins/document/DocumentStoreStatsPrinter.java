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
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|base
operator|.
name|Strings
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
name|inventory
operator|.
name|Format
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
name|inventory
operator|.
name|InventoryPrinter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_comment
comment|/**  * Inventory printer for {@link DocumentStore#getStats()}.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|property
operator|=
block|{
literal|"felix.inventory.printer.name=oak-document-store-stats"
block|,
literal|"felix.inventory.printer.title=Oak DocumentStore Statistics"
block|,
literal|"felix.inventory.printer.format=TEXT"
block|}
argument_list|)
specifier|public
class|class
name|DocumentStoreStatsPrinter
implements|implements
name|InventoryPrinter
block|{
annotation|@
name|Reference
name|DocumentNodeStore
name|nodeStore
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|print
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|Format
name|format
parameter_list|,
name|boolean
name|isZip
parameter_list|)
block|{
if|if
condition|(
name|format
operator|!=
name|Format
operator|.
name|TEXT
condition|)
block|{
return|return;
block|}
name|DocumentStore
name|store
init|=
name|nodeStore
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|printTitle
argument_list|(
name|pw
argument_list|,
literal|"DocumentStore metadata"
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|pw
argument_list|,
name|store
operator|.
name|getMetadata
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|()
expr_stmt|;
name|printTitle
argument_list|(
name|pw
argument_list|,
literal|"DocumentStore statistics"
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|pw
argument_list|,
name|store
operator|.
name|getStats
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|printTitle
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|String
name|title
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
name|Strings
operator|.
name|repeat
argument_list|(
literal|"="
argument_list|,
name|title
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|print
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
parameter_list|)
block|{
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|sortedData
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|sortedData
operator|.
name|forEach
argument_list|(
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
name|pw
operator|.
name|println
argument_list|(
name|k
operator|+
literal|"="
operator|+
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
