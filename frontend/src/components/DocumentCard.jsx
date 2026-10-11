import {
  FileText,
  Download,
  Share2,
  Trash2,
  Sparkles,
  ExternalLink,
  Tag,
  Clock,
  HardDrive
} from 'lucide-react';

const CATEGORY_STYLES = {
  INVOICE: 'bg-emerald-50 text-emerald-700 border-emerald-200',
  RESUME: 'bg-blue-50 text-blue-700 border-blue-200',
  ID_CARD: 'bg-purple-50 text-purple-700 border-purple-200',
  CONTRACT: 'bg-indigo-50 text-indigo-700 border-indigo-200',
  ACADEMIC: 'bg-cyan-50 text-cyan-700 border-cyan-200',
  MEDICAL: 'bg-rose-50 text-rose-700 border-rose-200',
  FINANCIAL: 'bg-amber-50 text-amber-700 border-amber-200',
  REPORT: 'bg-teal-50 text-teal-700 border-teal-200',
  OTHER: 'bg-slate-100 text-slate-700 border-slate-200'
};

export default function DocumentCard({ doc, onView, onShare, onDelete, onDownload }) {
  const formatBytes = (bytes) => {
    if (!bytes) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  };

  const formatDate = (isoString) => {
    if (!isoString) return '';
    const date = new Date(isoString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  };

  const badgeStyle = CATEGORY_STYLES[doc.category] || CATEGORY_STYLES.OTHER;

  return (
    <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-xs hover:border-indigo-200 hover:shadow-md transition-all flex flex-col justify-between group">
      <div>
        {/* Top Header: Category & Actions */}
        <div className="flex items-start justify-between gap-2">
          <span className={`text-[11px] font-bold px-2.5 py-0.5 rounded-full border ${badgeStyle} tracking-wide`}>
            {doc.category}
          </span>
          <div className="flex items-center space-x-1 opacity-90 group-hover:opacity-100 transition-opacity">
            <button
              onClick={() => onShare(doc)}
              title="Share Document"
              className="p-1.5 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors cursor-pointer"
            >
              <Share2 className="w-3.5 h-3.5" />
            </button>
            <button
              onClick={() => onDownload(doc)}
              title="Download File"
              className="p-1.5 text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors cursor-pointer"
            >
              <Download className="w-3.5 h-3.5" />
            </button>
            <button
              onClick={() => onDelete(doc)}
              title="Delete Document"
              className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors cursor-pointer"
            >
              <Trash2 className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>

        {/* Title and Filename */}
        <div className="mt-3">
          <h4
            onClick={() => onView(doc)}
            className="text-sm font-bold text-slate-900 group-hover:text-indigo-600 transition-colors line-clamp-1 cursor-pointer"
          >
            {doc.title}
          </h4>
          <p className="text-[11px] text-slate-400 truncate mt-0.5 font-mono">{doc.fileName}</p>
        </div>

        {/* AI Summary Highlight */}
        {doc.summary ? (
          <div className="mt-3 p-2.5 rounded-xl bg-amber-50/70 border border-amber-200/60 text-[11px] text-amber-900">
            <div className="flex items-center gap-1 font-semibold text-amber-800 mb-1">
              <Sparkles className="w-3 h-3 text-amber-600" />
              <span>AI Summary</span>
            </div>
            <p className="line-clamp-2 leading-relaxed text-slate-700">{doc.summary}</p>
          </div>
        ) : (
          <div className="mt-3 p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-[11px] text-slate-500 italic">
            No summary generated yet.
          </div>
        )}

        {/* Tags */}
        {doc.tags && (
          <div className="mt-3 flex flex-wrap gap-1">
            {doc.tags.split(',').slice(0, 3).map((tag, i) => (
              <span key={i} className="text-[10px] font-medium px-2 py-0.5 rounded bg-slate-100 text-slate-600 inline-flex items-center gap-1">
                <Tag className="w-2.5 h-2.5" /> {tag.trim()}
              </span>
            ))}
          </div>
        )}
      </div>

      {/* Footer Info */}
      <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-[11px] text-slate-400">
        <div className="flex items-center gap-1">
          <Clock className="w-3 h-3" />
          <span>{formatDate(doc.createdAt)}</span>
        </div>
        <div className="flex items-center gap-1">
          <HardDrive className="w-3 h-3" />
          <span>{formatBytes(doc.fileSize)}</span>
        </div>
        <button
          onClick={() => onView(doc)}
          className="text-indigo-600 hover:text-indigo-700 font-semibold inline-flex items-center gap-1 cursor-pointer"
        >
          <span>Insights</span>
          <ExternalLink className="w-3 h-3" />
        </button>
      </div>
    </div>
  );
}
