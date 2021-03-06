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
name|run
package|;
end_package

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
name|ImmutableMap
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
name|exporter
operator|.
name|NodeStateExportCommand
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
name|index
operator|.
name|IndexCommand
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
name|run
operator|.
name|commons
operator|.
name|Command
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
name|run
operator|.
name|commons
operator|.
name|Modes
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|AvailableModes
block|{
comment|// list of available Modes for the tool
specifier|public
specifier|static
specifier|final
name|Modes
name|MODES
init|=
operator|new
name|Modes
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Command
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"backup"
argument_list|,
operator|new
name|BackupCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"check"
argument_list|,
operator|new
name|CheckCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"checkpoints"
argument_list|,
operator|new
name|CheckpointsCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"clusternodes"
argument_list|,
operator|new
name|ClusterNodesCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"compact"
argument_list|,
operator|new
name|CompactCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"composite-prepare"
argument_list|,
operator|new
name|CompositePrepareCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"console"
argument_list|,
operator|new
name|ConsoleCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|DataStoreCommand
operator|.
name|NAME
argument_list|,
operator|new
name|DataStoreCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"datastorecacheupgrade"
argument_list|,
operator|new
name|DataStoreCacheUpgradeCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"datastorecheck"
argument_list|,
operator|new
name|DataStoreCheckCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
operator|new
name|DebugCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"explore"
argument_list|,
operator|new
name|ExploreCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|NodeStateExportCommand
operator|.
name|NAME
argument_list|,
operator|new
name|NodeStateExportCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"garbage"
argument_list|,
operator|new
name|GarbageCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"help"
argument_list|,
operator|new
name|HelpCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"history"
argument_list|,
operator|new
name|HistoryCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"index-merge"
argument_list|,
operator|new
name|IndexMergeCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IndexCommand
operator|.
name|NAME
argument_list|,
operator|new
name|IndexCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IOTraceCommand
operator|.
name|NAME
argument_list|,
operator|new
name|IOTraceCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|JsonIndexCommand
operator|.
name|INDEX
argument_list|,
operator|new
name|JsonIndexCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|PersistentCacheCommand
operator|.
name|PERSISTENTCACHE
argument_list|,
operator|new
name|PersistentCacheCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"rdbddldump"
argument_list|,
operator|new
name|RDBDDLDumpCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"recovery"
argument_list|,
operator|new
name|RecoveryCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"recover-journal"
argument_list|,
operator|new
name|RecoverJournalCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"revisions"
argument_list|,
operator|new
name|RevisionsCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"repair"
argument_list|,
operator|new
name|RepairCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"resetclusterid"
argument_list|,
operator|new
name|ResetClusterIdCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"restore"
argument_list|,
operator|new
name|RestoreCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"tarmkdiff"
argument_list|,
operator|new
name|FileStoreDiffCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|ThreadDumpCommand
operator|.
name|THREADDUMP
argument_list|,
operator|new
name|ThreadDumpCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"tika"
argument_list|,
operator|new
name|TikaCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"unlockupgrade"
argument_list|,
operator|new
name|UnlockUpgradeCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"upgrade"
argument_list|,
operator|new
name|UpgradeCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"search-nodes"
argument_list|,
operator|new
name|SearchNodesCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"segment-copy"
argument_list|,
operator|new
name|SegmentCopyCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"server"
argument_list|,
operator|new
name|ServerCommand
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"purge-index-versions"
argument_list|,
operator|new
name|PurgeOldIndexVersionCommand
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

