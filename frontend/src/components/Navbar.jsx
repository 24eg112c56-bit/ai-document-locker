import { ShieldCheck, Brain, LogOut, UploadCloud, User } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Navbar({ onOpenUpload, aiStatus }) {
  const { user, logout } = useAuth();

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200 shadow-xs">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-indigo-600 flex items-center justify-center text-white shadow-md shadow-indigo-100">
            <ShieldCheck className="w-6 h-6" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-lg font-bold text-slate-900 tracking-tight">AI Document Locker</span>
              <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-indigo-100 text-indigo-700 uppercase tracking-wider">
                Enterprise
              </span>
            </div>
            <p className="text-xs text-slate-500">Secure Cloud Storage with Gemini AI</p>
          </div>
        </div>

        {/* Right Actions */}
        <div className="flex items-center space-x-3">
          {/* AI Badge */}
          <div className="hidden sm:flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-50 text-amber-800 border border-amber-200">
            <Brain className="w-3.5 h-3.5 text-amber-600" />
            <span>{aiStatus?.aiConfigured ? 'Gemini 2.0 AI Live' : 'AI Engine Active'}</span>
          </div>

          {/* Upload Button */}
          <button
            onClick={onOpenUpload}
            className="inline-flex items-center gap-2 px-3.5 py-2 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 active:bg-indigo-800 rounded-lg shadow-xs shadow-indigo-200 transition-all cursor-pointer"
          >
            <UploadCloud className="w-4 h-4" />
            <span>Upload Document</span>
          </button>

          {/* User & Logout */}
          <div className="flex items-center pl-2 border-l border-slate-200 space-x-2">
            <div className="flex items-center space-x-2 text-xs text-slate-700 font-medium">
              <div className="w-8 h-8 rounded-full bg-slate-100 text-slate-600 flex items-center justify-center border border-slate-200">
                <User className="w-4 h-4" />
              </div>
              <div className="hidden md:block text-left">
                <div className="font-bold text-slate-900 leading-tight">{user?.fullName || 'User'}</div>
                <div className="text-[11px] text-slate-500 leading-tight">{user?.email}</div>
              </div>
            </div>
            <button
              onClick={logout}
              title="Sign Out"
              className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors cursor-pointer"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </header>
  );
}
