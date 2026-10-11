import { FileText, BrainCircuit, HardDrive, Share2 } from 'lucide-react';

export default function StatsCards({ stats }) {
  const formatBytes = (bytes) => {
    if (!bytes || bytes === 0) return '0 KB';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  };

  const cards = [
    {
      label: 'Total Documents',
      value: stats?.totalDocuments || 0,
      icon: FileText,
      color: 'bg-blue-50 text-blue-600 border-blue-200',
      description: 'Private files encrypted & stored'
    },
    {
      label: 'AI Analyzed',
      value: stats?.aiProcessedCount || 0,
      icon: BrainCircuit,
      color: 'bg-amber-50 text-amber-600 border-amber-200',
      description: 'Summarized & auto-classified'
    },
    {
      label: 'Storage Consumed',
      value: formatBytes(stats?.totalSizeBytes),
      icon: HardDrive,
      color: 'bg-emerald-50 text-emerald-600 border-emerald-200',
      description: 'Aiven DB & file locker'
    },
    {
      label: 'Active Shares',
      value: stats?.totalShares || 0,
      icon: Share2,
      color: 'bg-purple-50 text-purple-600 border-purple-200',
      description: 'Secure tokenized links'
    }
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card, idx) => {
        const Icon = card.icon;
        return (
          <div key={idx} className="bg-white rounded-xl border border-slate-200 p-5 shadow-xs hover:border-slate-300 transition-all">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">{card.label}</p>
                <p className="mt-1.5 text-2xl font-bold text-slate-900 tracking-tight">{card.value}</p>
              </div>
              <div className={`p-2.5 rounded-xl border ${card.color}`}>
                <Icon className="w-5 h-5" />
              </div>
            </div>
            <p className="mt-2 text-xs text-slate-400 font-medium">{card.description}</p>
          </div>
        );
      })}
    </div>
  );
}
